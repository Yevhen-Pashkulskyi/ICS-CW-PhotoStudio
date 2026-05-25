package com.example.control;

import com.example.dataDB.storage.ClientDAO;
import com.example.dataDB.storage.PhotoDAO;
import com.example.entity.Client;
import com.example.entity.Photo;
import com.example.entity.Photographer;
import com.example.model.Order;
import com.example.service.SessionType;
import com.example.util.OrderStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrderController {

    private final DatabaseManager dbManager;

    public OrderController(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    // Метод з діаграми: пошук клієнта
    public Client findClient(String phone) {
        return dbManager.getClientByPhone(phone);
    }

    // Метод з діаграми: створення нового замовлення (відкриття форми/діалогу)
    public void newOrder() {
        System.out.println("Ініціалізація нового замовлення...");
    }

    // Метод з діаграми: вільні фотографи
    public List<Photographer> getAvailablePhotographers(LocalDateTime dateTime){
        List<Order> orders = getOrders();
        return getPhotographers().stream()
                .filter(p -> orders.stream()
                        .noneMatch(o -> o.getPhotographer().getId().equals(p.getId())
                                && o.getEventDate().toLocalDateTime().toLocalDate().equals(dateTime.toLocalDate())))
                .collect(Collectors.toList());
    }

    // Метод з діаграми: розрахунок вартості
    public double calculateCost(Long sessionId, Long clientId) {
        // Шукаємо сесію і клієнта через dbManager, рахуємо ціну зі знижкою
        double basePrice = 1000.0; // Приклад завантаженої базової ціни
        boolean isRegular = true;  // Приклад перевірки статусу клієнта

        if (isRegular) {
            return basePrice * 0.90;
        }
        return basePrice;
    }

    // Метод з діаграми: фіналізація та збереження
    public void finalizeOrder(Order order, List<String> photoPath) throws IOException {
        boolean saved = dbManager.saveOrder(order);
        if (saved) {
            System.out.println("Замовлення успішно фіналізовано в системі!");
        }
        for (String path : photoPath) {
            Photo photo = new Photo(path, order.getId());
            new PhotoDAO().savePhoto(photo);
        }
    }

    // --- ЗАЛИШАЄМО КОРИСНЕ ЗІ СТАРОЇ ПРОГРАМИ (Для ReportsPanel) ---

    public long getActiveOrdersCount() {
        // Рахуємо замовлення зі статусом NEW або IN_PROGRESS
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
        return dbManager.getPhotosByOrderId(orderId);
    }

    public double getTotalRevenue() {
        // Рахуємо суму totalCost для замовлень за період
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

    // Геттери для Swing-таблиць, які тепер беруть дані через dbManager
    public List<Client> getClients() { return dbManager.getAllClients(); }
    public List<Order> getOrders() { return dbManager.getAllOrders(); }
    public List<Photographer> getPhotographers() { return dbManager.getAllPhotographers(); }
    public List<SessionType> getSessionTypes() { return dbManager.getAllSessionTypes(); }

    public void addClient(Client client) {
       new ClientDAO().saveClients(client);
    }

    // Додай цей метод в свій OrderController
    public void completeOrderPayment(Order order) throws Exception {
        // 1. Змінюємо статус об'єкта в Java
        order.setStatus(OrderStatus.PAID);

        // 2. Оновлюємо статус замовлення в базі даних PostgreSQL через твій dbManager (або OrderDAO)
        // Тобі знадобиться метод на кшталт dbManager.updateOrderStatus(order.getId(), OrderStatus.PAID);
        dbManager.updateOrderStatus(order.getId(), OrderStatus.PAID);

        // 3. Зберігаємо платіж в таблицю платежів (Payment)
        // Новий об'єкт Payment передається в PaymentDAO
        // new PaymentDAO().savePayment(new Payment(order.getId(), order.getTotalCost()));

        // 4. Перевірка лояльності: рахуємо скільки всього ОПЛАЧЕНИХ замовлень у цього клієнта
        long paidOrdersCount = dbManager.getAllOrders().stream()
                .filter(o -> o.getClient().getId().equals(order.getClient().getId()))
                .filter(o -> o.getStatus() == OrderStatus.PAID)
                .count();

        // Бізнес-правило: якщо це 3-є або більше оплачене замовлення — робимо його постійним
        if (paidOrdersCount >= 3 && !order.getClient().isRegular()) {
            order.getClient().setRegular(true);
            order.getClient().setDiscountRate(10.0); // 10% знижки на майбутнє

            // Оновлюємо клієнта в БД (робимо UPDATE в таблиці clients)
             new ClientDAO().updateClient(order.getClient());
        }
    }
}