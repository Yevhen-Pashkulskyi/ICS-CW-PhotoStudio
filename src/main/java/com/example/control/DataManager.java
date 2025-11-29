package com.example.control;

import com.example.entity.Client;
import com.example.entity.Photo;
import com.example.entity.Photographer;
import com.example.model.Order;
import com.example.service.InventoryItem;
import com.example.service.Persistable;
import com.example.service.SessionType;
import com.example.util.OrderStatus;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DataManager implements Persistable, Serializable {

    private List<Client> clients = new ArrayList<>();
    private List<Photographer> photographers = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();
    private List<SessionType> sessionTypes = new ArrayList<>();
    private List<InventoryItem> inventory = new ArrayList<>();

    private static final String DIR = ".";

    public DataManager() {
        try {
            loadDataFromFile(DIR);
        } catch (IOException e) {
            System.out.println("Дані не знайдено. Створення базових довідників...");
            initBaseData();
        }
    }

    private void initBaseData() {
        if (sessionTypes.isEmpty()) {
            sessionTypes.add(new SessionType("Портрет", 1000));
            sessionTypes.add(new SessionType("Весілля", 5000));
            sessionTypes.add(new SessionType("Сімейна", 1500));
        }
        if (photographers.isEmpty()) {
            photographers.add(new Photographer("Олег Вінник", "0991112233", "Весілля"));
            photographers.add(new Photographer("Даша Астаф'єва", "0995556677", "Портрет"));
            photographers.add(new Photographer("Денис Голоборотько", "0975556677", "Сімейна"));
        }
    }

    // --- Логіка пошуку та перевірок ---

    public Client findClientByPhone(String phone) {
        return clients.stream()
                .filter(c -> c.getPhoneNumber().equals(phone))
                .findFirst()
                .orElse(null);
    }

    // --- Методи додавання ---
    public void addClient(Client c) {
        clients.add(c);
        saveAllQuietly();
    }

    public void addOrder(Order o) {
        orders.add(o);
        saveAllQuietly();
    }

    public void addPhotographer(Photographer p) {
        photographers.add(p);
        saveAllQuietly();
    }

    private void saveAllQuietly() {
        try {
            saveDataToFile(DIR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 1. Активні замовлення
    public long getActiveOrdersCount() {
        return orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.NEW || o.getStatus() == OrderStatus.IN_PROGRESS)
                .count();
    }

    // 2. Статистика клієнтів
    public long getRegularClientsCount() {
        return clients.stream().filter(Client::isRegular).count();
    }
    public long getNewClientsCount() {
        return clients.stream().filter(c -> !c.isRegular()).count();
    }

    // 3. Фотографи
    public int getPhotographersCount() {
        return photographers.size();
    }

    public List<Photographer> getAvailablePhotographers(LocalDateTime date) {
        List<Photographer> available = new ArrayList<>();
        for (Photographer p : photographers) {
            boolean isBusy = orders.stream()
                    .filter(o -> o.getPhotographer().getId().equals(p.getId()))
                    .anyMatch(o -> {
                        LocalDateTime oDate = o.getOrderDate();
                        return oDate.toLocalDate().isEqual(date.toLocalDate()) &&
                                Math.abs(oDate.getHour() - date.getHour()) < 2;
                    });
            if (!isBusy) available.add(p);
        }
        return available;
    }

    // 4. Фото
    public List<Photo> getPhotosForOrder(String id) {
        return orders.stream()
                .filter(o -> o.getId().equals(id))
                .findFirst()
                .map(Order::getPhotos)
                .orElse(new ArrayList<>());
    }

    // 5. Дохід
    public double getTotalRevenueForPeriod(LocalDateTime start, LocalDateTime end) {
        return orders.stream()
                .filter(o -> !o.getOrderDate().isBefore(start) && !o.getOrderDate().isAfter(end))
                .mapToDouble(Order::getTotalCost)
                .sum();
    }

    // 6. Популярна послуга
    public Optional<String> getMostPopularSessionType() {
        return orders.stream()
                .collect(Collectors.groupingBy(o -> o.getSessionType().getName(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    // --- CSV Implementation ---
    @Override
    public void saveDataToFile(String path) throws IOException {
        try (PrintWriter w = new PrintWriter(new FileWriter(path + "/clients.csv"))) {
            for (Client c : clients)
                w.println(c.getId() + "," + c.getName() + "," + c.getPhoneNumber() + "," + c.getEmail() + "," + c.isRegular());
        }
        try (PrintWriter w = new PrintWriter(new FileWriter(path + "/photographers.csv"))) {
            for (Photographer p : photographers)
                w.println(p.getId() + "," + p.getName() + "," + p.getPhoneNumber() + "," + p.getSpecialization());
        }
        try (PrintWriter w = new PrintWriter(new FileWriter(path + "/orders.csv"))) {
            for (Order o : orders)
                w.println(o.getId() + "," + o.getOrderDate().toString() + "," + o.getStatus() + "," +
                        o.getClient().getId() + "," + o.getPhotographer().getId() + "," + o.getSessionType().getName() + "," + o.getTotalCost());
        }
        try (PrintWriter w = new PrintWriter(new FileWriter(path + "/photos.csv"))) {
            for (Order o : orders) {
                for (Photo photo : o.getPhotos()) {
                    w.println(photo.getId() + "," + o.getId() + "," + photo.getFilePath());
                }
            }
        }
    }

    @Override
    public void loadDataFromFile(String path) throws IOException {
        clients.clear();
        photographers.clear();
        orders.clear();

        File f1 = new File(path + "/clients.csv");
        if (f1.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(f1))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] p = line.split(",");
                    if (p.length >= 5) {
                        Client c = new Client(p[1], p[2], p[3], Boolean.parseBoolean(p[4]));
                        c.setId(p[0]);
                        clients.add(c);
                    }
                }
            }
        }

        File f2 = new File(path + "/photographers.csv");
        if (f2.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(f2))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] p = line.split(",");
                    if (p.length >= 4) {
                        Photographer ph = new Photographer(p[1], p[2], p[3]);
                        ph.setId(p[0]);
                        photographers.add(ph);
                    }
                }
            }
        }
        if (photographers.isEmpty()) initBaseData();

        File f3 = new File(path + "/orders.csv");
        if (f3.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(f3))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] p = line.split(",");
                    if (p.length >= 7) {
                        Client c = clients.stream().filter(i -> i.getId().equals(p[3])).findFirst().orElse(null);
                        Photographer ph = photographers.stream().filter(i -> i.getId().equals(p[4])).findFirst().orElse(null);
                        if (c != null && ph != null) {
                            SessionType st = new SessionType(p[5], Double.parseDouble(p[6]));
                            Order o = new Order(c, ph, st);
                            o.setId(p[0]);
                            o.setOrderDate(LocalDateTime.parse(p[1]));
                            o.setStatus(OrderStatus.valueOf(p[2]));
                            o.setTotalCost(Double.parseDouble(p[6]));
                            orders.add(o);
                        }
                    }
                }
            }
        }

        // === 4. НОВЕ: ЗАВАНТАЖЕННЯ ФОТОГРАФІЙ ===
        File f4 = new File(path + "/photos.csv");
        if (f4.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(f4))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] p = line.split(",");
                    if (p.length >= 3) {
                        String photoId = p[0];
                        String orderId = p[1];
                        String filePath = p[2];

                        // Знаходимо замовлення, до якого належить це фото
                        Optional<Order> orderOpt = orders.stream()
                                .filter(o -> o.getId().equals(orderId))
                                .findFirst();

                        if (orderOpt.isPresent()) {
                            Photo photo = new Photo(filePath);
                             photo.setId(photoId);
                            orderOpt.get().getPhotos().add(photo);
                        }
                    }
                }
            }
        }
    }

    /**
     * Перевіряє, чи існує клієнт з таким телефоном або email.
     * @return true, якщо дублікат знайдено.
     */
    public boolean clientExists(String phone, String email) {
        return clients.stream().anyMatch(c ->
                c.getPhoneNumber().equals(phone) ||
                        (email != null && !email.isEmpty() && c.getEmail().equalsIgnoreCase(email))
        );
    }

    /**
     * Перевіряє історію клієнта.
     * Якщо у нього 3 або більше оплачених замовлень, робить його постійним.
     */
    public void checkAndUpgradeClient(Client client) {
        // Якщо він вже постійний - нічого не робимо
        if (client.isRegular()) return;

        long paidOrdersCount = orders.stream()
                .filter(o -> o.getClient().getId().equals(client.getId())) // Замовлення цього клієнта
                .filter(o -> o.getStatus() == OrderStatus.PAID)            // Тільки оплачені
                .count();

        // Поріг - 3 замовлення
        if (paidOrdersCount >= 3) {
            client.setRegular(true);
            System.out.println("Вітаємо! Клієнт " + client.getName() + " став постійним!");
            saveAllQuietly(); // Зберігаємо зміни у файл
        }
    }

    public List<Client> getClients() { return clients; }
    public List<Photographer> getPhotographers() { return photographers; }
    public List<Order> getOrders() { return orders; }
    public List<SessionType> getSessionTypes() { return sessionTypes; }
}