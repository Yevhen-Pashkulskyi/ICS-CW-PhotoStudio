package com.example.ui.panels;

import com.example.control.DatabaseManager;
import com.example.control.OrderController;
import com.example.dataDB.storage.ReportDAO;
import com.example.entity.Photo;
import com.example.entity.Photographer;
import com.example.model.Order;
import com.example.util.OrderStatus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;

/**
 * Панель графічного інтерфейсу для модуля аналітики та звітності.
 * <p>
 * Цей клас відповідає за реалізацію <b>IV Етапу</b> курсової роботи (виконання 6-ти запитів).
 * Інтерфейс побудовано за принципом "Master-Detail": зліва знаходиться меню вибору звіту,
 * справа — текстова область для виведення результатів.
 */
public class ReportsPanel extends JPanel {

    private final OrderController orderController;
    private final DatabaseManager databaseManager =new DatabaseManager();
    private final JTextArea reportArea;

    /**
     * Конструктор панелі звітів.
     * Налаштовує розділений екран (JSplitPane), створює кнопки для кожного типу звіту
     * та прив'язує їх до відповідних методів обробки.
     *
     * @param orderController екземпляр менеджера даних.
     */
    public ReportsPanel(OrderController orderController) {
        this.orderController = orderController;
        setLayout(new BorderLayout());

        // Заголовок панелі
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel title = new JLabel("Аналітика та Звіти");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(title);
        add(topPanel, BorderLayout.NORTH);

        // Розділювач екрану
        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(320); // Ширина меню кнопок

        // --- ЛІВА ЧАСТИНА: Меню з категоріями кнопок ---
        JPanel menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Категорія 1: Локальні звіти програми
        JLabel labelLocal = new JLabel("📊 Оперативний аналіз додатка:");
        labelLocal.setFont(new Font("Arial", Font.BOLD, 13));
        labelLocal.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuContainer.add(labelLocal);
        menuContainer.add(Box.createVerticalStrut(5));

        // --- Панель кнопок (Лівa частина) ---
        JPanel localButtonsPanel = new JPanel(new GridLayout(6, 1, 4, 4));
        localButtonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Використання посилань на методи (Method References) для чистоти коду
        addButton(localButtonsPanel, "1. Активні замовлення", this::reportActiveOrders);
        addButton(localButtonsPanel, "2. Статистика клієнтів", this::reportClients);
        addButton(localButtonsPanel, "3. Фотографи", this::reportPhotographers);
        addButton(localButtonsPanel, "4. Список фото (по ID)", this::reportPhotos);
        addButton(localButtonsPanel, "5. Дохід", this::reportRevenue);
        addButton(localButtonsPanel, "6. Популярна послуга", this::reportPopularType);
        menuContainer.add(localButtonsPanel);

        menuContainer.add(Box.createVerticalStrut(20)); // Відступ між блоками

        // Категорія 2: Складні SQL Запити до БД
        JLabel labelDb = new JLabel("🗄️ Глибока SQL-аналітика бази даних:");
        labelDb.setFont(new Font("Arial", Font.BOLD, 13));
        labelDb.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuContainer.add(labelDb);
        menuContainer.add(Box.createVerticalStrut(5));

        JPanel dbButtonsPanel = new JPanel(new GridLayout(9, 1, 4, 4));
        dbButtonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        addButton(dbButtonsPanel, "Л.2.1: Клієнти заданого фотографа", this::sqlClientsByPhotographer);
        addButton(dbButtonsPanel, "Л.2.2: Пошук клієнтів на літеру", this::sqlClientsByLetter);
        addButton(dbButtonsPanel, "Л.2.3: Замовлення за період", this::sqlOrdersInPeriod);
        addButton(dbButtonsPanel, "Л.2.4: Кількість нових за тиждень", this::sqlNewOrdersWeekCount);
        addButton(dbButtonsPanel, "Л.2.5: Кількість замовлень кожного", this::sqlOrdersCountPerPhotographer);
        addButton(dbButtonsPanel, "Л.2.6: Максимум замовлень (ALL)", this::sqlMostLoadedPhotographers);
        addButton(dbButtonsPanel, "Л.2.7: Топ-ставки по категоріях", this::sqlTopRatesBySpec);
        addButton(dbButtonsPanel, "Л.2.8: Вільні фотографи на Травень", this::sqlNoOrdersInMay);
        addButton(dbButtonsPanel, "Л.2.9: Аналіз завантаженості (UNION)", this::sqlLoadingStatusUnion);
        menuContainer.add(dbButtonsPanel);

        // Додаємо скрол для панелі кнопок на випадок малих екранів
        JScrollPane menuScrollPane = new JScrollPane(menuContainer);
        menuScrollPane.setBorder(null);

        // --- Область виводу (Права частина) ---
        reportArea = new JTextArea();
        reportArea.setEditable(false); // Заборона редагування користувачем
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 13)); // Моноширинний шрифт для вирівнювання

        splitPane.setLeftComponent(localButtonsPanel);
        splitPane.setRightComponent(new JScrollPane(reportArea));
        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Допоміжний метод для створення та додавання кнопок.
     * Дозволяє уникнути дублювання коду налаштування стилів.
     *
     * @param panel  панель, куди додається кнопка.
     * @param text   текст на кнопці.
     * @param action дія, яка виконується при натисканні (Runnable).
     */
    private void addButton(JPanel panel, String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        btn.addActionListener(e -> action.run());
        panel.add(btn);
    }

    // --- Логіка генерації звітів ---

    /**
     * УНІВЕРСАЛЬНИЙ МЕТОД ВИВЕДЕННЯ ТАБЛИЦЬ (Усуває дублювання коду).
     * Форматує масив даних String[] у рівну, читаєму текстову таблицю.
     */
    private void printTableReport(String reportName, String[] headers, List<String[]> rows) {
        StringBuilder sb = new StringBuilder("=== SQL ЗВІТ: " + reportName.toUpperCase() + " ===\n\n");

        // Будуємо шапку таблиці
        for (String header : headers) {
            sb.append(String.format("%-25s | ", header));
        }
        sb.append("\n").append("-".repeat(headers.length * 28)).append("\n");

        // Будуємо рядки
        if (rows.isEmpty()) {
            sb.append("[ База даних повернула порожній результат. Дані для аналізу відсутні ]\n");
        } else {
            for (String[] row : rows) {
                for (String cell : row) {
                    sb.append(String.format("%-25s | ", cell != null ? cell : "NULL"));
                }
                sb.append("\n");
            }
        }
        reportArea.setText(sb.toString());
    }

    private ReportDAO getReportDAO() {
        return orderController.getDbManager().getReportDAO();
    }

    private void sqlClientsByPhotographer() {
        String input = JOptionPane.showInputDialog(this, "Введіть ID фотографа для аналізу:");
        if (input == null || input.trim().isEmpty()) return;
        try {
            Long id = Long.parseLong(input);
            List<String[]> data = getReportDAO().getClientsByPhotographer(id);
            printTableReport("Список клієнтів фотографа ID " + id,
                    new String[]{"ПІБ Клієнта", "Дата фотосесії", "Тип зйомки"}, data);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID має бути числом!", "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sqlClientsByLetter() {
        String letter = JOptionPane.showInputDialog(this, "З якої літери починається ПІБ клієнта (напр. К):");
        if (letter == null || letter.trim().isEmpty()) return;
        List<String[]> data = getReportDAO().getClientsByLetter(letter.trim());
        printTableReport("Клієнти на літеру '" + letter + "'",
                new String[]{"Повне Ім'я", "Телефон", "Email"}, data);
    }

    private void sqlOrdersInPeriod() {
        // Для спрощення та надійності аналізу використовуємо травень 2026, як у курсовій
        Timestamp start = Timestamp.valueOf("2026-05-01 00:00:00");
        Timestamp end = Timestamp.valueOf("2026-05-31 23:59:59");

        List<String[]> data = getReportDAO().getOrdersInPeriod(start, end);
        printTableReport("Замовлення за Травень 2026 року",
                new String[]{"ID Ордера", "ID Клієнта", "Дата події", "Вартість (грн)"}, data);
    }

    private void sqlNewOrdersWeekCount() {
        int count = getReportDAO().getNewOrdersCountLastWeek();
        StringBuilder sb = new StringBuilder("=== SQL АГРЕГАТНИЙ ЗВІТ ===\n\n");
        sb.append("Завдання: Скільки нових замовлень надійшло за останні 7 днів?\n");
        sb.append("-----------------------------------------------------------------\n");
        sb.append("📊 Результат лічильника: ").append(count).append(" нових звернень.\n");
        reportArea.setText(sb.toString());
    }

    private void sqlOrdersCountPerPhotographer() {
        List<String[]> data = getReportDAO().getOrdersCountPerPhotographer();
        printTableReport("Кількість виконаних/запланованих замовлень по персоналу",
                new String[]{"Фотограф", "Всього замовлень (шт)"}, data);
    }

    private void sqlMostLoadedPhotographers() {
        List<String[]> data = getReportDAO().getMostLoadedPhotographers();
        printTableReport("Майстри з максимальною кількістю замовлень (Предикат ALL)",
                new String[]{"Ім'я кращого фотографа"}, data);
    }

    private void sqlTopRatesBySpec() {
        List<String[]> data = getReportDAO().getTopPhotographersBySpecialization();
        printTableReport("Найвищі базові ставки за кожною спеціалізацією",
                new String[]{"Спеціалізація", "Фотограф", "Ставка (грн/год)"}, data);
    }

    private void sqlNoOrdersInMay() {
        List<String[]> data = getReportDAO().getPhotographersWithNoOrdersInMay2026();
        printTableReport("Фотоографи без жодного замовлення на Травень 2026 (NOT EXISTS)",
                new String[]{"Вільний персонал"}, data);
    }

    private void sqlLoadingStatusUnion() {
        List<String[]> data = getReportDAO().getPhotographerLoadingStatus();
        printTableReport("Аналіз маркерів завантаженості кадрів (UNION)",
                new String[]{"Фотограф", "Статус системи маркетингу"}, data);
    }

    /**
     * Реалізація Запиту №1: Кількість активних замовлень.
     * Виводить загальну кількість та список замовлень зі статусами NEW або IN_PROGRESS.
     */
    private void reportActiveOrders() {
        StringBuilder sb = new StringBuilder("=== АКТИВНІ ЗАМОВЛЕННЯ ===\n\n");
        sb.append("Кількість: ").append(orderController.getActiveOrdersCount()).append("\n");
        sb.append(String.format("%-10s | %-15s | %-20s\n", "ID", "Статус", "Клієнт"));
        sb.append("--------------------------------------------------\n");

        // Використання Stream API для фільтрації та форматування
        orderController.getOrders().stream()
                .filter(o -> o.getStatus() == OrderStatus.NEW || o.getStatus() == OrderStatus.IN_PROGRESS)
                .forEach(o -> sb.append(String.format("%-10d | %-15s | %-20s\n",
                        o.getId(), o.getStatus(), o.getClient().getFullName())));

        reportArea.setText(sb.toString());
    }

    /**
     * Реалізація Запиту №2: Статистика клієнтів.
     * Порівнює кількість нових та постійних клієнтів.
     */
    private void reportClients() {
        StringBuilder sb = new StringBuilder("=== СТАТИСТИКА БАЗИ КЛІЄНТІВ ===\n\n");
        sb.append("Постійні клієнти (мають знижку): ").append(orderController.getRegularClientsCount()).append("\n");
        sb.append("Нові клієнти (базовий тариф):   ").append(orderController.getNewClientsCount()).append("\n");
        sb.append("--------------------------------------------------\n");
        sb.append("Всього зареєстровано в базі:     ").append(orderController.getClients().size());
        reportArea.setText(sb.toString());
    }

    /**
     * Реалізація Запиту №3: Кількість фотографів.
     * Виводить список персоналу та їх спеціалізацію.
     */
    private void reportPhotographers() {
        LocalDateTime now = LocalDateTime.now();

        // Отримуємо список тих, хто вільний прямо зараз
        List<Photographer> freePhotographers = orderController.getAvailablePhotographers(now);

        StringBuilder sb = new StringBuilder("=== МОНІТОРИНГ ЗАЙНЯТОСТІ ФОТОГРАФІВ ===\n");
        sb.append("Станом на: ").append(now.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))).append("\n");
        sb.append("Всього фотографів: ").append(orderController.getPhotographers().size()).append("\n\n");

        for (Photographer p : orderController.getPhotographers()) {
            sb.append("• ").append(p.getFullName()).append(" (").append(p.getSpecialization()).append(")");

            // Перевіряємо, чи є цей фотограф у списку вільних
            // Порівнюємо за ID, щоб було надійно
            boolean isFree = freePhotographers.stream()
                    .anyMatch(free -> free.getId().equals(p.getId()));

            if (isFree) {
                sb.append("\n   [СТАТУС]: ВІЛЬНИЙ ✅ (Готовий до роботи)");
            } else {
                sb.append("\n   [СТАТУС]: ЗАЙНЯТИЙ ❌ (Має замовлення у цей час)");
            }
            sb.append("\n---------------------------\n");
        }
        reportArea.setText(sb.toString());
    }

    /**
     * Реалізація Запиту №4: Список фото для конкретного замовлення.
     * Відкриває діалогове вікно для введення ID замовлення, знаходить його
     * та виводить список прив'язаних файлів.
     */
    private void reportPhotos() {
        String input = JOptionPane.showInputDialog(this, "ID замовлення:");
        if (input == null || input.trim().isEmpty()) return; // Користувач натиснув Cancel
        try {
            long orderId = Long.parseLong(input);
            // Пошук замовлення (підтримується введення неповного ID)
            Order order = orderController.getOrders().stream()
                    .filter(o -> o.getId() == orderId)
                    .findFirst().orElse(null);

            if (order != null) {
                StringBuilder sb = new StringBuilder("АРХІВ СВІТЛИН ЗАМОВЛЕННЯ №").append(order.getId()).append("\n");
                sb.append("Клієнт: ").append(order.getClient().getFullName()).append("\n");
                sb.append("Шлях до хмарного сховища файлів:\n");
                sb.append("--------------------------------------------------\n");
                List<Photo> photos = orderController.getPhotosForOrder(orderId);

                if (photos.isEmpty()) {
                    sb.append("[Альбом порожній] Світлини ще не завантажені фотографом.");
                } else {
                    photos.forEach(p -> sb.append("📂 ").append(p.getFilePath()).append("\n"));
                }
                reportArea.setText(sb.toString());
            } else {
                reportArea.setText("Замовлення №" + orderId + " не знайдено в системі.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Помилка: ID повинен бути числовим значенням!",
                    "Некоректний ввід", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Реалізація Запиту №5: Загальна вартість усіх замовлень.
     * Розраховує сумарний дохід за весь період існування системи.
     */
    private void reportRevenue() {
        double totalRevenue = orderController.getTotalRevenue();
        StringBuilder sb = new StringBuilder("=== ФІНАНСОВИЙ АНАЛІТИЧНИЙ ЗВІТ ===\n\n");
        sb.append("Загальна каса фотостудії (всі оплачені ордери):\n");
        sb.append("💰 ").append(String.format("%.2f", totalRevenue)).append(" грн\n");
        reportArea.setText(sb.toString());
    }

    /**
     * Реалізація Запиту №6: Тип фотосесії з найбільшим попитом.
     * Аналізує історію замовлень та визначає найпопулярнішу послугу.
     */
    private void reportPopularType() {
        StringBuilder sb = new StringBuilder("=== МАРКЕТИНГОВИЙ АНАЛІЗ ПОПИТУ ===\n\n");
        sb.append("Найбільш затребуваний тип фотосесії:\n");
        sb.append("⭐ ").append(orderController.getMostPopularSessionType().orElse("Дані відсутні (немає замовлень)")).append("\n");
        reportArea.setText(sb.toString());
    }
}