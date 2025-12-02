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

/**
 * Центральний контролер для управління всіма даними системи (патерн Singleton/Service).
 * Відповідає за зберігання списків сутностей у пам'яті, їх обробку (пошук, фільтрація),
 * а також за персистентність (збереження та завантаження з файлів CSV).
 */
public class DataManager implements Persistable, Serializable {

    // Списки для зберігання даних у пам'яті (in-memory database)
    private List<Client> clients = new ArrayList<>();
    private List<Photographer> photographers = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();
    private List<SessionType> sessionTypes = new ArrayList<>(); // Довідник типів послуг
    private List<InventoryItem> inventory = new ArrayList<>();

    // Шлях до кореневої папки для зберігання файлів
    private static final String DIR = ".";

    /**
     * Конструктор.
     * При ініціалізації намагається завантажити дані з файлів.
     * Якщо файли відсутні, створює базові тестові дані.
     */
    public DataManager() {
        try {
            loadDataFromFile(DIR);
        } catch (IOException e) {
            System.out.println("Дані не знайдено. Створення базових довідників...");
            initBaseData();
        }
    }

    /**
     * Ініціалізує систему початковими даними (типи сесій, фотографи),
     * якщо файли даних порожні або відсутні.
     */
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

    /**
     * Знаходить клієнта за номером телефону.
     * @param phone номер телефону для пошуку.
     * @return знайдений об'єкт Client або null, якщо не знайдено.
     */
    public Client findClientByPhone(String phone) {
        return clients.stream()
                .filter(c -> c.getPhoneNumber().equals(phone))
                .findFirst()
                .orElse(null);
    }

    // --- Методи додавання даних ---

    /**
     * Додає нового клієнта до списку та зберігає зміни.
     * @param c об'єкт клієнта.
     */
    public void addClient(Client c) {
        clients.add(c);
        saveAllQuietly();
    }

    /**
     * Додає нове замовлення до списку та зберігає зміни.
     * @param o об'єкт замовлення.
     */
    public void addOrder(Order o) {
        orders.add(o);
        saveAllQuietly();
    }

    /**
     * Додає нового фотографа до системи.
     * @param p об'єкт фотографа.
     */
    public void addPhotographer(Photographer p) {
        photographers.add(p);
        saveAllQuietly();
    }

    /**
     * Допоміжний метод для збереження даних без необхідності обробки виключень
     * у коді виклику. Використовується після кожної модифікації даних.
     */
    private void saveAllQuietly() {
        try {
            saveDataToFile(DIR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- Методи для генерації звітів (Аналітика) ---

    /**
     * Повертає кількість активних замовлень (статуси NEW або IN_PROGRESS).
     * @return кількість активних замовлень.
     */
    public long getActiveOrdersCount() {
        return orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.NEW || o.getStatus() == OrderStatus.IN_PROGRESS)
                .count();
    }

    /**
     * Повертає кількість постійних клієнтів (isRegular = true).
     */
    public long getRegularClientsCount() {
        return clients.stream().filter(Client::isRegular).count();
    }

    /**
     * Повертає кількість нових клієнтів (isRegular = false).
     */
    public long getNewClientsCount() {
        return clients.stream().filter(c -> !c.isRegular()).count();
    }

    /**
     * Повертає загальну кількість зареєстрованих фотографів.
     */
    public int getPhotographersCount() {
        return photographers.size();
    }

    /**
     * Повертає список фотографів, які вільні на вказану дату та час.
     * (Логіка перевірки перетину замовлень в межах +/- 2 годин).
     * @param date бажана дата та час сесії.
     * @return список доступних фотографів.
     */
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

    /**
     * Повертає список фотографій, прив'язаних до конкретного замовлення.
     * @param id унікальний ідентифікатор замовлення.
     * @return список об'єктів Photo.
     */
    public List<Photo> getPhotosForOrder(String id) {
        return orders.stream()
                .filter(o -> o.getId().equals(id))
                .findFirst()
                .map(Order::getPhotos)
                .orElse(new ArrayList<>());
    }

    /**
     * Розраховує загальну суму виручки (totalCost) за вказаний період.
     * @param start початок періоду.
     * @param end кінець періоду.
     * @return сума доходу.
     */
    public double getTotalRevenueForPeriod(LocalDateTime start, LocalDateTime end) {
        return orders.stream()
                .filter(o -> !o.getOrderDate().isBefore(start) && !o.getOrderDate().isAfter(end))
                .mapToDouble(Order::getTotalCost)
                .sum();
    }

    /**
     * Визначає назву типу фотосесії, який користується найбільшим попитом.
     * @return Optional з назвою найпопулярнішого типу.
     */
    public Optional<String> getMostPopularSessionType() {
        return orders.stream()
                .collect(Collectors.groupingBy(o -> o.getSessionType().getName(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    // --- Реалізація інтерфейсу Persistable (Робота з файлами) ---

    /**
     * Зберігає всі колекції даних у відповідні CSV-файли.
     * @param path шлях до папки для збереження.
     * @throws IOException у разі помилок запису.
     */
    @Override
    public void saveDataToFile(String path) throws IOException {
        // Збереження клієнтів
        try (PrintWriter w = new PrintWriter(new FileWriter(path + "/clients.csv"))) {
            for (Client c : clients)
                w.println(c.getId() + "," + c.getName() + "," + c.getPhoneNumber() + "," + c.getEmail() + "," + c.isRegular());
        }
        // Збереження фотографів
        try (PrintWriter w = new PrintWriter(new FileWriter(path + "/photographers.csv"))) {
            for (Photographer p : photographers)
                w.println(p.getId() + "," + p.getName() + "," + p.getPhoneNumber() + "," + p.getSpecialization());
        }
        // Збереження замовлень (з ID замість вкладених об'єктів)
        try (PrintWriter w = new PrintWriter(new FileWriter(path + "/orders.csv"))) {
            for (Order o : orders)
                w.println(o.getId() + "," + o.getOrderDate().toString() + "," + o.getStatus() + "," +
                        o.getClient().getId() + "," + o.getPhotographer().getId() + "," + o.getSessionType().getName() + "," + o.getTotalCost());
        }
        // Збереження фотографій (зв'язок "один до багатьох")
        try (PrintWriter w = new PrintWriter(new FileWriter(path + "/photos.csv"))) {
            for (Order o : orders) {
                for (Photo photo : o.getPhotos()) {
                    w.println(photo.getId() + "," + o.getId() + "," + photo.getFilePath());
                }
            }
        }
    }

    /**
     * Завантажує дані з CSV-файлів та відновлює об'єктні зв'язки.
     * @param path шлях до папки з файлами.
     * @throws IOException у разі помилок читання.
     */
    @Override
    public void loadDataFromFile(String path) throws IOException {
        clients.clear();
        photographers.clear();
        orders.clear();

        // 1. Завантаження клієнтів
        File f1 = new File(path + "/clients.csv");
        if (f1.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(f1))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] p = line.split(",");
                    if (p.length >= 5) {
                        Client c = new Client(p[1], p[2], p[3], Boolean.parseBoolean(p[4]));
                        c.setId(p[0]); // Відновлення ID
                        clients.add(c);
                    }
                }
            }
        }

        // 2. Завантаження фотографів
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
        // Якщо база фотографів порожня (перший запуск або видалення), додаємо базових
        if (photographers.isEmpty()) initBaseData();

        // 3. Завантаження замовлень та відновлення зв'язків
        File f3 = new File(path + "/orders.csv");
        if (f3.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(f3))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] p = line.split(",");
                    if (p.length >= 7) {
                        // Пошук об'єктів за ID
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

        // 4. Завантаження фотографій для замовлень
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

                        // Знаходимо замовлення за ID
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
     * Перевіряє, чи існує вже клієнт з таким номером телефону або email.
     * Використовується для запобігання дублювання записів.
     * @param phone номер телефону.
     * @param email електронна пошта.
     * @return true, якщо клієнт знайдений.
     */
    public boolean clientExists(String phone, String email) {
        return clients.stream().anyMatch(c ->
                c.getPhoneNumber().equals(phone) ||
                        (email != null && !email.isEmpty() && c.getEmail().equalsIgnoreCase(email))
        );
    }

    /**
     * Перевіряє історію замовлень клієнта.
     * Якщо клієнт має 3 або більше оплачених замовлень, йому автоматично
     * присвоюється статус "Постійний клієнт" (знижка 10%).
     * @param client об'єкт клієнта для перевірки.
     */
    public void checkAndUpgradeClient(Client client) {
        if (client.isRegular()) return;

        long paidOrdersCount = orders.stream()
                .filter(o -> o.getClient().getId().equals(client.getId()))
                .filter(o -> o.getStatus() == OrderStatus.PAID)
                .count();

        if (paidOrdersCount >= 3) {
            client.setRegular(true);
            System.out.println("Клієнт " + client.getName() + " отримав статус постійного!");
            saveAllQuietly();
        }
    }

    // Геттери для доступу до колекцій (для UI)
    public List<Client> getClients() { return clients; }
    public List<Photographer> getPhotographers() { return photographers; }
    public List<Order> getOrders() { return orders; }
    public List<SessionType> getSessionTypes() { return sessionTypes; }
}