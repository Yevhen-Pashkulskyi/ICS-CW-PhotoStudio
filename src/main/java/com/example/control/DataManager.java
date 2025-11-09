package com.example.control;

import com.example.entity.Client;
import com.example.entity.Photo;
import com.example.entity.Photographer;
import com.example.model.Order;
import com.example.service.InventoryItem;
import com.example.service.Persistable;
import com.example.util.OrderStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервісний клас для управління всіма даними системи.
 * Реалізує інтерфейс Persistable та інкапсулює колекції.
 */
public class DataManager implements Persistable, Serializable {

    // Використання колекцій для зберігання даних
    private List<Client> clients;
    private List<Photographer> photographers;
    private List<Order> orders;
    private List<InventoryItem> inventory;

    public DataManager() {
        // Ініціалізація колекцій
        this.clients = new ArrayList<>();
        this.photographers = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.inventory = new ArrayList<>();
    }

    // --- Методи для додавання в колекції ---
    public void addClient(Client client) {
        this.clients.add(client);
    }

    public void addOrder(Order order) {
        this.orders.add(order);
    }

    // --- Реалізація 6-ти запитів з використанням колекцій (Stream API) ---

    // 1. Кількість активних замовлень
    public long getActiveOrdersCount() {
        return orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.NEW || o.getStatus() == OrderStatus.IN_PROGRESS)
                .count();
    }

    // 2. Кількість клієнтів (постійних)
    public long getRegularClientsCount() {
        return clients.stream().filter(Client::isRegular).count();
    }

    // 2. Кількість клієнтів (нових)
    public long getNewClientsCount() {
        return clients.stream().filter(c -> !c.isRegular()).count();
    }

    // 3. Кількість фотографів (реалізація логіки вільних слотів у Schedule)
    public int getPhotographersCount() {
        return photographers.size();
    }

    // 4. Список фото для конкретного замовлення
    public List<Photo> getPhotosForOrder(String orderId) {
        return orders.stream()
                .filter(o -> o.getId().equals(orderId))
                .findFirst()
                .map(Order::getPhotos)
                .orElse(new ArrayList<>());
    }

    // 5. Загальна вартість усіх замовлень за період
    public double getTotalRevenueForPeriod(LocalDateTime start, LocalDateTime end) {
        return orders.stream()
                .filter(o -> !o.getOrderDate().isBefore(start) && !o.getOrderDate().isAfter(end))
                .mapToDouble(Order::getTotalCost)
                .sum();
    }

    // 6. Тип фотосесії з найбільшим попитом
    public Optional<String> getMostPopularSessionType() {
        return orders.stream()
                .collect(Collectors.groupingBy(o -> o.getSessionType().getName(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    @Override
    public void saveDataToFile(String path) {
        // Логіка збереження у JSON буде тут
    }

    @Override
    public void loadDataFromFile(String path) {
        // Логіка читання з JSON буде тут
    }

    // Геттери для списків (для відображення в GUI)
    public List<Client> getClients() { return clients; }
    public List<Photographer> getPhotographers() { return photographers; }
    public List<Order> getOrders() { return orders; }
}
