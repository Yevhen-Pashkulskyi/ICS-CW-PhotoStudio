import java.util.*;
import java.io.*;
import java.time.*;
import java.util.stream.Collectors;

// Інтерфейси
interface Persistable {
    void saveToFile(String path) throws IOException;
    void loadFromFile(String path) throws IOException;
}

interface Calculable {
    float calculateCost();
    float applyDiscount(float amount);
}

interface Manageable {
    void create();
    void update();
    void delete();
    Object findById(String id);
}

// Абстрактний клас Subject
abstract class Subject {
    protected String id;
    protected String name;

    public Subject(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String n) {
        this.name = n;
    }

    public abstract String getContactInfo();
}

// Клас Client
class Client extends Subject {
    private String email;
    private String phone;
    private boolean isRegular;

    public Client(String id, String name, String email, String phone, boolean isRegular) {
        super(id, name);
        this.email = email;
        this.phone = phone;
        this.isRegular = isRegular;
    }

    public void createClient() {
        // Логіка створення
    }

    public void updateClient() {
        // Логіка оновлення
    }

    public boolean isRegular() {
        return isRegular;
    }

    @Override
    public String getContactInfo() {
        return "Email: " + email + ", Phone: " + phone;
    }
}

// Клас Photographer
class Photographer extends Subject {
    private List<Date> availability;

    public Photographer(String id, String name, List<Date> availability) {
        super(id, name);
        this.availability = availability;
    }

    public List<Date> getAvailability() {
        return availability;
    }

    public void updateAvailability(List<Date> a) {
        this.availability = a;
    }

    @Override
    public String getContactInfo() {
        return "Availability: " + availability.toString();
    }

    public int getFreeSlotsCount() {
        // Припустимо, що availability - список вільних дат
        return availability.size();
    }
}

// Абстрактний клас AbstractPayment
abstract class AbstractPayment implements Calculable {
    protected String id;
    protected String orderId;
    protected float amount;
    protected LocalDate date;
    public static final float TAX_RATE = 0.2f; // Статична

    public AbstractPayment(String id, String orderId, float amount) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.date = LocalDate.now();
    }

    public void registerPayment(float a) {
        this.amount = a;
    }

    @Override
    public float calculateCost() {
        return amount * (1 + TAX_RATE);
    }

    @Override
    public float applyDiscount(float discountPercent) {
        return amount * (1 - discountPercent / 100);
    }

    abstract void processPayment(float a);
}

// Похідний клас CashPayment
class CashPayment extends AbstractPayment {
    private float cashAmount;
    private float change;

    public CashPayment(String id, String orderId, float amount) {
        super(id, orderId, amount);
    }

    @Override
    void processPayment(float cash) {
        this.cashAmount = cash;
        this.change = cash - amount;
    }

    public float getChange() {
        return change;
    }
}

// Клас Order
class Order {
    private String id;
    private String clientId;
    private String sessionTypeId;
    private Date date;
    private String time; // Змінено на String для спрощення
    private float estimatedCost;
    private String status;
    private float finalCost;
    private String paymentStatus;
    private PhotoDetails photoDetails; // Композиція
    private List<Photo> photos = new ArrayList<>();

    public Order(String id, String clientId, String sessionTypeId) {
        this.id = id;
        this.clientId = clientId;
        this.sessionTypeId = sessionTypeId;
        this.status = "New";
    }

    public void setSessionType(String type) {
        this.sessionTypeId = type;
    }

    public void setDateTime(Date d, String t) {
        // Валідація: дата не в минулому
        if (d.before(new Date())) throw new IllegalArgumentException("Дата в минулому");
        this.date = d;
        this.time = t;
    }

    public void setEstimatedCost(float c) {
        if (c < 0) throw new IllegalArgumentException("Вартість негативна");
        this.estimatedCost = c;
    }

    public void finalizeOrder() {
        this.status = "Finalized";
    }

    public void markAsCompleted() {
        this.status = "Completed";
    }

    public void setPhotoDetails(PhotoDetails pd) {
        this.photoDetails = pd;
    }

    public void addPhoto(Photo p) {
        photos.add(p);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    // Геттери для полів
    public String getId() { return id; }
    public Date getDate() { return date; }
    public float getFinalCost() { return finalCost; }
    public void setFinalCost(float fc) { this.finalCost = fc; }
    public String getStatus() { return status; }
    public String getSessionTypeId() { return sessionTypeId; }
}

// Клас PhotoDetails
class PhotoDetails {
    private String id;
    private String orderId;
    private int quantity;
    private String style;

    public PhotoDetails(String id, String orderId, int quantity, String style) {
        this.id = id;
        this.orderId = orderId;
        this.quantity = quantity;
        this.style = style;
    }

    public int getQuantity() {
        return quantity;
    }
}

// Сервіс CostCalculator
class CostCalculator implements Calculable {
    public static float calculateCost(PhotoDetails pd, List<PricingRule> pr, boolean isDiscount) {
        float base = pd.getQuantity() * 10; // Приклад
        for (PricingRule rule : pr) {
            base *= rule.multiplier;
        }
        if (isDiscount) base = base * 0.9f;
        return base;
    }

    @Override
    public float calculateCost() {
        return 0; // Заглушка
    }

    @Override
    public float applyDiscount(float amount) {
        return amount * 0.9f;
    }
}

// Клас PricingRule
class PricingRule {
    private String id;
    private String description;
    public float multiplier;

    public PricingRule(String id, String description, float multiplier) {
        this.id = id;
        this.description = description;
        this.multiplier = multiplier;
    }

    public static List<PricingRule> getRules() {
        return Arrays.asList(new PricingRule("1", "Base", 1.0f), new PricingRule("2", "Premium", 1.5f));
    }
}

// Клас ClientCollection
class ClientCollection implements Persistable {
    private List<Client> clientsList = new ArrayList<>();
    private int size;

    public void addClient(Client c) {
        clientsList.add(c);
        size++;
    }

    public Client findById(String id) {
        for (Client c : clientsList) {
            if (c.getId().equals(id)) return c;
        }
        return null;
    }

    @Override
    public void saveToFile(String path) throws IOException {
        // Приклад: серіалізація в JSON (використовуй Gson у реальному коді)
        try (FileWriter writer = new FileWriter(path)) {
            writer.write("{\"clients\": " + clientsList.size() + "}"); // Заглушка
        }
    }

    @Override
    public void loadFromFile(String path) throws IOException {
        // Заглушка
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            // Читання
        }
    }

    public int getRegularClientsCount() {
        return (int) clientsList.stream().filter(Client::isRegular).count();
    }

    public int getNewClientsCount() {
        return size - getRegularClientsCount();
    }
}

// Клас PhotographerCollection
class PhotographerCollection implements Persistable {
    private List<Photographer> photographersList = new ArrayList<>();
    private int size;

    public void addPhotographer(Photographer p) {
        photographersList.add(p);
        size++;
    }

    public Photographer findById(String id) {
        for (Photographer p : photographersList) {
            if (p.getId().equals(id)) return p;
        }
        return null;
    }

    @Override
    public void saveToFile(String path) throws IOException {
        // Заглушка
        try (FileWriter writer = new FileWriter(path)) {
            writer.write("{\"photographers\": " + photographersList.size() + "}");
        }
    }

    @Override
    public void loadFromFile(String path) throws IOException {
        // Заглушка
    }

    public int getPhotographersCount() {
        return size;
    }

    public int getTotalFreeSlots() {
        return photographersList.stream().mapToInt(Photographer::getFreeSlotsCount).sum();
    }
}

// Клас OrderCollection
class OrderCollection implements Persistable {
    private List<Order> ordersList = new ArrayList<>();
    private int size;

    public List<Order> getOrdersList() {
        return ordersList;
    }

    public void addOrder(Order o) {
        ordersList.add(o);
        size++;
    }

    public Order findById(String id) {
        for (Order o : ordersList) {
            if (o.getId().equals(id)) return o;
        }
        return null;
    }

    @Override
    public void saveToFile(String path) throws IOException {
        // Заглушка
        try (FileWriter writer = new FileWriter(path)) {
            writer.write("{\"orders\": " + ordersList.size() + "}");
        }
    }

    @Override
    public void loadFromFile(String path) throws IOException {
        // Заглушка
    }

    public int getActiveOrdersCount() {
        return (int) ordersList.stream().filter(o -> "Active".equals(o.getStatus()) || "New".equals(o.getStatus()) || "Finalized".equals(o.getStatus())).count();
    }

    public float getTotalCostBetween(Date start, Date end) {
        return (float) ordersList.stream()
                .filter(o -> o.getDate() != null && o.getDate().after(start) && o.getDate().before(end))
                .mapToDouble(Order::getFinalCost)
                .sum();
    }

    public String getMostPopularSessionType() {
        if (ordersList.isEmpty()) return null;
        Map<String, Long> countMap = ordersList.stream()
                .collect(Collectors.groupingBy(Order::getSessionTypeId, Collectors.counting()));
        return countMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}

// Клас SessionType
class SessionType {
    private String id;
    private String name;
    private float basePrice;

    public static List<SessionType> getAvailableSessionTypes() {
        return Arrays.asList(new SessionType("1", "Portrait", 100), new SessionType("2", "Family", 200));
    }

    public SessionType(String id, String name, float basePrice) {
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
    }

    public String getId() {
        return id;
    }
}

// Клас Schedule
class Schedule {
    private String id;
    private String photographerId;
    private Date date;
    private String timeSlot;
    private boolean isAvailable;

    public Schedule(String id, String photographerId, Date date, String timeSlot, boolean isAvailable) {
        this.id = id;
        this.photographerId = photographerId;
        this.date = date;
        this.timeSlot = timeSlot;
        this.isAvailable = isAvailable;
    }

    public boolean checkAvailability(Date d, String t) {
        return date.equals(d) && timeSlot.equals(t) && isAvailable;
    }

    public void reserveSlot() {
        isAvailable = false;
    }
}

// Клас OrderStatus
class OrderStatus {
    private String id;
    private String orderId;
    private String status;
    private Date updateDate;

    public OrderStatus(String id, String orderId, String status, Date updateDate) {
        this.id = id;
        this.orderId = orderId;
        this.status = status;
        this.updateDate = updateDate;
    }

    public void updateStatus(String s) {
        this.status = s;
        this.updateDate = new Date();
    }

    public boolean isPhotoReady() {
        return "Ready".equals(status);
    }
}

// Клас Photo
class Photo {
    private String id;
    private String orderId;
    private String filePath;
    private boolean isReady;

    public Photo(String id, String orderId, String filePath, boolean isReady) {
        this.id = id;
        this.orderId = orderId;
        this.filePath = filePath;
        this.isReady = isReady;
    }

    public void issuePhoto() {
        // Логіка видачі
    }
}

// Клас Discount
class Discount {
    private String id;
    private float percentage;
    private String condition;

    public Discount(String id, float percentage, String condition) {
        this.id = id;
        this.percentage = percentage;
        this.condition = condition;
    }

    public float applyDiscount(float amount) {
        return amount * (1 - percentage / 100);
    }
}

// Клас TaxCalculator
class TaxCalculator implements Calculable {
    public static float calculateTax(float amount) {
        return amount * AbstractPayment.TAX_RATE;
    }

    @Override
    public float calculateCost() {
        return 0;
    }

    @Override
    public float applyDiscount(float amount) {
        return 0;
    }
}

// Клас Receipt
class Receipt {
    private String id;
    private String orderId;
    private String details;

    public Receipt(String id, String orderId, String details) {
        this.id = id;
        this.orderId = orderId;
        this.details = details;
    }

    public String generateReceipt() {
        return "Receipt: " + details;
    }
}

// Клас OrderManager
class OrderManager implements Manageable {
    public OrderCollection orderCollection = new OrderCollection();
    public ClientCollection clientCollection = new ClientCollection();
    public PhotographerCollection photographerCollection = new PhotographerCollection();

    public void newOrder(String clientId, String sessionTypeId) {
        Order o = new Order(UUID.randomUUID().toString(), clientId, sessionTypeId);
        orderCollection.addOrder(o);
    }

    public boolean isSessionTypeAvailable(String type) {
        List<SessionType> types = SessionType.getAvailableSessionTypes();
        return types.stream().anyMatch(t -> t.getId().equals(type));
    }

    public boolean isSlotAvailable(Date d, String t) {
        // Логіка перевірки
        return true;
    }

    public float getCost(int q, String s) {
        // Логіка
        return 100.0f;
    }

    public void finalizeOrder(String orderId) {
        Order o = orderCollection.findById(orderId);
        if (o != null) o.finalizeOrder();
    }

    public Order openOrder(String id) {
        return orderCollection.findById(id);
    }

    public String checkStatus(String orderId) {
        Order o = orderCollection.findById(orderId);
        return o != null ? o.getStatus() : null;
    }

    public float getFinalCost(String orderId) {
        Order o = orderCollection.findById(orderId);
        return o != null ? o.getFinalCost() : 0;
    }

    public void processPayment(String orderId, float amount) {
        // Логіка
    }

    public String generateReceipt(String orderId) {
        // Логіка
        return "Receipt generated";
    }

    @Override
    public void create() {}
    @Override
    public void update() {}
    @Override
    public void delete() {}
    @Override
    public Object findById(String id) {
        return orderCollection.findById(id);
    }
}

// Приклад використання
public class Main {
    public static void main(String[] args) {
        OrderManager manager = new OrderManager();

        // Додавання клієнтів
        Client c1 = new Client("c1", "Client1", "email1@example.com", "123456", true);
        Client c2 = new Client("c2", "Client2", "email2@example.com", "654321", false);
        manager.clientCollection.addClient(c1);
        manager.clientCollection.addClient(c2);

        System.out.println("Regular clients: " + manager.clientCollection.getRegularClientsCount());
        System.out.println("New clients: " + manager.clientCollection.getNewClientsCount());

        // Додавання фотографів
        List<Date> avail1 = Arrays.asList(new Date(), new Date(System.currentTimeMillis() + 86400000));
        Photographer p1 = new Photographer("p1", "Photographer1", avail1);
        manager.photographerCollection.addPhotographer(p1);

        System.out.println("Photographers count: " + manager.photographerCollection.getPhotographersCount());
        System.out.println("Total free slots: " + manager.photographerCollection.getTotalFreeSlots());

        // Додавання замовлень
        manager.newOrder("c1", "1");
        Order o1 = manager.openOrder(manager.orderCollection.getOrdersList().get(0).getId());
        o1.setDateTime(new Date(), "10:00");
        o1.setEstimatedCost(100);
        o1.setFinalCost(100);
        o1.setStatus("Active");

        manager.newOrder("c2", "2");
        Order o2 = manager.openOrder(manager.orderCollection.getOrdersList().get(1).getId());
        o2.setDateTime(new Date(System.currentTimeMillis() + 86400000), "11:00");
        o2.setEstimatedCost(200);
        o2.setFinalCost(200);
        o2.setStatus("Active");

        System.out.println("Active orders: " + manager.orderCollection.getActiveOrdersCount());

        // Список фото
        Photo ph1 = new Photo("ph1", o1.getId(), "path1", true);
        o1.addPhoto(ph1);
        System.out.println("Photos for order: " + o1.getPhotos().size());

        // Загальна вартість
        Date start = new Date(System.currentTimeMillis() - 86400000);
        Date end = new Date(System.currentTimeMillis() + 86400000 * 2);
        System.out.println("Total cost: " + manager.orderCollection.getTotalCostBetween(start, end));

        // Найпопулярніший тип
        System.out.println("Most popular session type: " + manager.orderCollection.getMostPopularSessionType());
    }
}
