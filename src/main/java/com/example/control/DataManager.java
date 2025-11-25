package com.example.control;

import com.example.entity.Client;
import com.example.entity.Photo;
import com.example.entity.Photographer;
import com.example.model.Order;
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
    private List<SessionType> sessionTypes = new ArrayList<>(); // Довідник послуг

    // Шлях до папки з даними
    private static final String DIR = ".";

    public DataManager() {
        // При створенні одразу пробуємо завантажити дані
        try {
            loadDataFromFile(DIR);
        } catch (IOException e) {
            System.out.println("Дані не знайдено. Створення базових довідників...");
            initBaseData();
        }
    }

    // Якщо файлів немає, створюємо стартові послуги і фотографів, щоб програма не була пустою
    private void initBaseData() {
        if (sessionTypes.isEmpty()) {
            sessionTypes.add(new SessionType("Портрет", 1000));
            sessionTypes.add(new SessionType("Весілля", 5000));
            sessionTypes.add(new SessionType("Сімейна", 1500));
        }
        if (photographers.isEmpty()) {
            photographers.add(new Photographer("Олег Вінник", "0991112233", "Весілля"));
            photographers.add(new Photographer("Даша Астаф'єва", "0995556677", "Портрет"));
        }
    }

    // --- Логіка пошуку та перевірок ---

    public Client findClientByPhone(String phone) {
        return clients.stream()
                .filter(c -> c.getPhoneNumber().equals(phone))
                .findFirst()
                .orElse(null);
    }

    /**
     * Повертає список фотографів, які ВІЛЬНІ в заданий час.
     * Спрощена логіка: фотограф зайнятий, якщо у нього є замовлення в цю дату +/- 2 години.
     */
    public List<Photographer> getAvailablePhotographers(LocalDateTime date) {
        List<Photographer> available = new ArrayList<>();

        for (Photographer p : photographers) {
            boolean isBusy = orders.stream()
                    .filter(o -> o.getPhotographer().getId().equals(p.getId()))
                    .anyMatch(o -> {
                        LocalDateTime oDate = o.getOrderDate();
                        // Перевіряємо, чи перетинається час (в межах 2 годин)
                        return oDate.toLocalDate().isEqual(date.toLocalDate()) &&
                                Math.abs(oDate.getHour() - date.getHour()) < 2;
                    });

            if (!isBusy) {
                available.add(p);
            }
        }
        return available;
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

    // Тихо зберігає (без викидання помилки наверх, для зручності)
    private void saveAllQuietly() {
        try {
            saveDataToFile(DIR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- CSV Implementation ---

    @Override
    public void saveDataToFile(String path) throws IOException {
        // 1. Клієнти
        try (PrintWriter w = new PrintWriter(new FileWriter(path + "/clients.csv"))) {
            for (Client c : clients)
                w.println(c.getId() + "," + c.getName() + "," + c.getPhoneNumber() + "," + c.getEmail() + "," + c.isRegular());
        }
        // 2. Фотографи
        try (PrintWriter w = new PrintWriter(new FileWriter(path + "/photographers.csv"))) {
            for (Photographer p : photographers)
                w.println(p.getId() + "," + p.getName() + "," + p.getPhoneNumber() + "," + p.getSpecialization());
        }
        // 3. Замовлення
        try (PrintWriter w = new PrintWriter(new FileWriter(path + "/orders.csv"))) {
            for (Order o : orders)
                w.println(o.getId() + "," + o.getOrderDate().toString() + "," + o.getStatus() + "," +
                        o.getClient().getId() + "," + o.getPhotographer().getId() + "," + o.getSessionType().getName() + "," + o.getTotalCost());
        }
    }

    @Override
    public void loadDataFromFile(String path) throws IOException {
        clients.clear();
        photographers.clear();
        orders.clear();

        // 1. Клієнти
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

        // 2. Фотографи
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

        // Якщо фотографів немає з файлу, ініціалізуємо базових
        if (photographers.isEmpty()) initBaseData();

        // 3. Замовлення
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
                            // Ціна (p[6]) і Назва послуги (p[5])
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
    }

    // Геттери списків
    public List<Client> getClients() {
        return clients;
    }

    public List<Photographer> getPhotographers() {
        return photographers;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public List<SessionType> getSessionTypes() {
        return sessionTypes;
    }

    // --- Методи для звітів (ті самі, що були) ---
    public long getActiveOrdersCount() {
        return orders.stream().filter(o -> o.getStatus() == OrderStatus.NEW || o.getStatus() == OrderStatus.IN_PROGRESS).count();
    }

    public long getRegularClientsCount() {
        return clients.stream().filter(Client::isRegular).count();
    }

    public long getNewClientsCount() {
        return clients.stream().filter(c -> !c.isRegular()).count();
    }

    public double getTotalRevenueForPeriod(LocalDateTime start, LocalDateTime end) {
        return orders.stream().filter(o -> !o.getOrderDate().isBefore(start) && !o.getOrderDate().isAfter(end)).mapToDouble(Order::getTotalCost).sum();
    }

    public Optional<String> getMostPopularSessionType() {
        return orders.stream().collect(Collectors.groupingBy(o -> o.getSessionType().getName(), Collectors.counting())).entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey);
    }

    public List<Photo> getPhotosForOrder(String id) {
        return orders.stream().filter(o -> o.getId().equals(id)).findFirst().map(Order::getPhotos).orElse(new ArrayList<>());
    }
}