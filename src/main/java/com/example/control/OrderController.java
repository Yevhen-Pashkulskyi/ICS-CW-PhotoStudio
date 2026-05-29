package com.example.control;

import com.example.entity.Client;
import com.example.entity.Photo;
import com.example.entity.Photographer;
import com.example.entity.Payment;
import com.example.entity.Order;
import com.example.entity.SessionType;
import com.example.util.OrderStatus;
import com.example.util.PaymentMethod;
import lombok.Getter;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrderController {

    @Getter
    private final DatabaseManager databaseManager;

    public OrderController(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public Client findClient(String phone) {
        return databaseManager.getClientByPhone(phone);
    }

    public List<Photographer> getAvailablePhotographers(LocalDateTime dateTime){
        List<Order> orders = getOrders();
        return getPhotographers().stream()
                .filter(p -> orders.stream()
                        .noneMatch(o -> o.getPhotographer().getId().equals(p.getId())
                                && o.getEventDate().toLocalDateTime().toLocalDate().equals(dateTime.toLocalDate())))
                .collect(Collectors.toList());
    }

    public void addOrder(Order order) {
        if (!databaseManager.saveOrder(order)) {
            throw new RuntimeException("Не вдалося зберегти замовлення в БД.");
        }
    }

    public void addClient(Client client) {
        databaseManager.saveClient(client);
    }

    public void finalizeOrder(Order order, List<String> photoPaths) throws IOException {
        addOrder(order); // Використовуємо вже існуючий метод
        for (String path : photoPaths) {
            Photo photo = new Photo(path, order.getId());
            databaseManager.savePhoto(photo);
        }
    }

    public void completeOrderPayment(Order order) throws Exception {
        // 1. Змінюємо статус
        order.setStatus(OrderStatus.PAID);
        databaseManager.updateOrderStatus(order.getId(), OrderStatus.PAID);

        // 2. РЕЗЕРВУЄМО ТА ЗБЕРІГАЄМО ПЛАТІЖ
        Payment payment = new Payment();
        payment.setOrderId(order);
        payment.setPaymentAmount(order.getTotalCost());
        payment.setPaymentDate(new Timestamp(System.currentTimeMillis()));
        payment.setPaymentMethod(PaymentMethod.CASH); // Дефолт, або можна передавати з UI
        databaseManager.savePayment(payment);

        // 3. Програма лояльності
        long paidOrdersCount = databaseManager.getAllOrders().stream()
                .filter(o -> o.getClient().getId().equals(order.getClient().getId()))
                .filter(o -> o.getStatus() == OrderStatus.PAID)
                .count();

        if (paidOrdersCount >= 3 && !order.getClient().isRegular()) {
            order.getClient().setRegular(true);
            order.getClient().setDiscountRate(10.0);
            databaseManager.updateClient(order.getClient());
        }
    }

    // --- Методи оперативної аналітики для ReportsPanel ---

    public long getActiveOrdersCount() {
        return getOrders().stream()
                .filter(o -> o.getStatus() == OrderStatus.NEW || o.getStatus() == OrderStatus.IN_PROGRESS)
                .count();
    }

    public long getRegularClientsCount() {
        return getClients().stream().filter(Client::isRegular).count();
    }

    public long getNewClientsCount() {
        return getClients().stream().filter(c -> !c.isRegular()).count();
    }

    public List<Photo> getPhotosForOrder(Long orderId) {
        return databaseManager.getPhotosByOrderId(orderId);
    }

    public double getTotalRevenue() {
        return getOrders().stream()
                .filter(o -> o.getStatus() == OrderStatus.PAID)
                .mapToDouble(Order::getTotalCost)
                .sum();
    }

    public Optional<String> getMostPopularSessionType() {
        return getOrders().stream()
                .collect(Collectors.groupingBy(o -> o.getSessionType().getSessionName(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    public List<Client> getClients() { return databaseManager.getAllClients(); }
    public List<Order> getOrders() { return databaseManager.getAllOrders(); }
    public List<Photographer> getPhotographers() { return databaseManager.getAllPhotographers(); }
    public List<SessionType> getSessionTypes() { return databaseManager.getAllSessionTypes(); }
    public int getClientsCount() {
        return databaseManager.getAllClients().size();
    }
    public int getOrdersCount() {
        return databaseManager.getAllOrders().size();
    }

}