package com.example.control;

import com.example.entity.Client;
import com.example.entity.Photo;
import com.example.entity.Photographer;
import com.example.model.Order;
import com.example.service.InventoryItem;
import com.example.service.Persistable;
import com.example.service.SessionType;
import com.example.util.OrderStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    public void addPhotographer(Photographer p) {
        this.photographers.add(p);
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

    /**
     * Зберігає дані у текстові файли формату CSV.
     * @param directoryPath Шлях до папки, де будуть створені файли.
     */
    @Override
    public void saveDataToFile(String directoryPath) throws IOException {
        // 1. Збереження клієнтів
        try (PrintWriter writer = new PrintWriter(new FileWriter(directoryPath + "/clients.csv"))) {
            for (Client c : clients) {
                // Формуємо рядок: id,name,phone,email,isRegular
                writer.println(c.getId() + "," + c.getName() + "," +
                        c.getPhoneNumber() + "," + c.getEmail() + "," + c.isRegular());
            }
        }

        // 2. Збереження фотографів
        try (PrintWriter writer = new PrintWriter(new FileWriter(directoryPath + "/photographers.csv"))) {
            for (Photographer p : photographers) {
                writer.println(p.getId() + "," + p.getName() + "," +
                        p.getPhoneNumber() + "," + p.getSpecialization());
            }
        }

        // 3. Збереження замовлень
        try (PrintWriter writer = new PrintWriter(new FileWriter(directoryPath + "/orders.csv"))) {
            for (Order o : orders) {
                // Зберігаємо ID пов'язаних об'єктів (Foreign Keys), а не самі об'єкти
                String clientId = o.getClient().getId();
                String photoId = o.getPhotographer().getId();
                String sessionName = o.getSessionType().getName();

                writer.println(o.getId() + "," + o.getOrderDate() + "," + o.getStatus() + "," +
                        clientId + "," + photoId + "," + sessionName + "," + o.getTotalCost());
            }
        }
    }

    /**
     * Завантажує дані з CSV файлів та відновлює зв'язки між об'єктами.
     * @param directoryPath Шлях до папки з файлами.
     */
    @Override
    public void loadDataFromFile(String directoryPath) throws IOException {
        // Очищуємо поточні списки перед завантаженням
        this.clients.clear();
        this.photographers.clear();
        this.orders.clear();

        // 1. Завантаження клієнтів
        File clientFile = new File(directoryPath + "/clients.csv");
        if (clientFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(clientFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(","); // Розділяємо рядок по комі
                    if (parts.length >= 5) {
                        // Створюємо об'єкт (конструктор має бути адаптований або використовуємо сеттери)
                        Client c = new Client(parts[1], parts[2], parts[3], Boolean.parseBoolean(parts[4]));
                        // Важливо: відновити "рідний" ID, а не генерувати новий
                        // (для цього в класі Person треба додати метод setId, або конструктор з ID)
                        // Тут для спрощення вважаємо, що ми просто створили нового з тими ж даними
                        c.setId(parts[0]);
                        this.clients.add(c);
                    }
                }
            }
        }

        // 2. Завантаження фотографів (аналогічно)
        File photoFile = new File(directoryPath + "/photographers.csv");
        if (photoFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(photoFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        Photographer p = new Photographer(parts[1], parts[2], parts[3]);
                        p.setId(parts[0]);
                        this.photographers.add(p);
                    }
                }
            }
        }

        // 3. Завантаження замовлень (найскладніше - відновлення зв'язків)
        File orderFile = new File(directoryPath + "/orders.csv");
        if (orderFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(orderFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 7) {
                        String clientId = parts[3];
                        String photoId = parts[4];
                        String sessionName = parts[5];

                        // Знаходимо реальні об'єкти в завантажених списках
                        // (У реальному коді тут потрібен пошук за ID,
                        // тут спрощено беремо першого знайденого або null)
                        Client client = findClientById(clientId);
                        Photographer photographer = findPhotographerById(photoId);

                        if (client != null && photographer != null) {
                            SessionType session = new SessionType(sessionName, 0); // Ціну можна брати з довідника
                            Order order = new Order(client, photographer, session);
                            // Відновлюємо статус і дату
                            order.setId(parts[0]);
                            order.setOrderDate(LocalDateTime.parse(parts[1]));
                            order.setStatus(OrderStatus.valueOf(parts[2]));
                            order.setTotalCost(Double.parseDouble(parts[6]));

                            this.orders.add(order);
                        }
                    }
                }
            }
        }
    }

    // Допоміжні методи для пошуку (мають бути реалізовані в DataManager)
    private Client findClientById(String id) {
        // Реальний пошук по ID. Для CSV це критично, щоб відновити зв'язок.
        // Оскільки ми в прикладі зберегли ID, але при завантаженні згенерували нові UUID,
        // то зв'язок може бути втрачено.
        // *Примітка для студента:* Щоб це працювало ідеально, треба додати в Person
        // метод protected void setId(String id) і викликати його при завантаженні.
        return clients.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst().orElse(null);
    }

    private Photographer findPhotographerById(String id) {
        return photographers.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst().orElse(null);
    }

    // Геттери для списків (для відображення в GUI)
    public List<Client> getClients() {
        return clients;
    }

    public List<Photographer> getPhotographers() {
        return photographers;
    }

    public List<Order> getOrders() {
        return orders;
    }
}
