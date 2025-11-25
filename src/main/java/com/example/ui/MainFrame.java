package com.example.ui;

import com.example.control.DataManager;
import com.example.entity.Client;
import com.example.entity.Payment;
import com.example.entity.Photo;
import com.example.entity.Photographer;
import com.example.model.Order;
import com.example.util.OrderStatus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class MainFrame extends JFrame {

    private DataManager dataManager;
    private static final String DATA_DIR_PATH = "."; // Поточна папка

    // Компоненти для перемикання екранів
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JTextArea reportArea;

    private DefaultTableModel orderTableModel;
    private DefaultTableModel clientTableModel;

    public MainFrame() {
        // 1. Налаштування вікна
        setTitle("Фотоательє IS - Курсова робота");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 2. Ініціалізація даних
        dataManager = new DataManager();
        loadData(); // Завантажуємо CSV при старті

        // 3. Створення бічного меню (Sidebar)
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // 4. Створення центральної панелі (Content)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Відступи
        contentPanel.setBackground(new Color(245, 245, 250)); // Світло-сірий фон

        // --- ДОДАВАННЯ ЕКРАНІВ (ПАНЕЛЕЙ) ---

        // Екран 1: Головна (Dashboard)
        contentPanel.add(createDashboardPanel(), "DASHBOARD");

        // Екран 2: Замовлення (ТЕПЕР ГОТОВИЙ)
        contentPanel.add(createOrdersPanel(), "ORDERS");

        // Екран 3: Клієнти
        contentPanel.add(createClientsPanel(), "CLIENTS");

        // Екран 4: Звіти
        contentPanel.add(createReportsPanel(), "REPORTS");

        add(contentPanel, BorderLayout.CENTER);
    }

    // --- МЕТОДИ СТВОРЕННЯ UI ---

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(50, 60, 80)); // Темний колір меню
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setBorder(new EmptyBorder(20, 10, 20, 10));

        // Заголовок меню
        JLabel titleLabel = new JLabel("ФОТОАТЕЛЬЄ");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(titleLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 40))); // Відступ

        // Кнопки меню
        sidebar.add(createMenuButton("Головна", "DASHBOARD"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createMenuButton("Замовлення", "ORDERS"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createMenuButton("Клієнти", "CLIENTS"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createMenuButton("Звіти (6 запитів)", "REPORTS"));

        // Розпірка, щоб притиснути кнопку виходу до низу
        sidebar.add(Box.createVerticalGlue());

        JButton exitBtn = new JButton("Зберегти та Вийти");
        styleButton(exitBtn);
        exitBtn.setBackground(new Color(200, 80, 80));
        exitBtn.addActionListener(e -> {
            saveData();
            System.exit(0);
        });
        sidebar.add(exitBtn);

        return sidebar;
    }

    private JButton createMenuButton(String text, String cardName) {
        JButton btn = new JButton(text);
        styleButton(btn);
        btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        return btn;
    }

    private void styleButton(JButton btn) {
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setBackground(new Color(70, 80, 100));
        btn.setForeground(Color.BLACK);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // --- ЕКРАН 1: ГОЛОВНА (DASHBOARD) ---
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false); // Прозорий, щоб було видно фон contentPanel

        // Вітання
        JLabel welcomeLabel = new JLabel("Вітаємо в системі управління!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(60, 60, 60));
        panel.add(welcomeLabel, BorderLayout.NORTH);

        // Центральна частина з великою кнопкою
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        JButton newOrderBtn = new JButton("+ НОВЕ ЗАМОВЛЕННЯ");
        newOrderBtn.setPreferredSize(new Dimension(300, 80));
        newOrderBtn.setFont(new Font("Arial", Font.BOLD, 20));
        newOrderBtn.setBackground(new Color(40, 167, 69)); // Зелений
        newOrderBtn.setForeground(Color.BLACK);
        newOrderBtn.setFocusPainted(false);

        // Дія: Відкрити діалог створення замовлення
        newOrderBtn.addActionListener(e -> openOrderDialog());

        centerPanel.add(newOrderBtn);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    // --- ЕКРАН 2: ЗАМОВЛЕННЯ ---
    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 1. Верхня панель
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel title = new JLabel("Управління замовленнями");
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JButton refreshBtn = new JButton("Оновити список");
        refreshBtn.addActionListener(e -> refreshOrderTable());

        topPanel.add(title);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(refreshBtn);

        panel.add(topPanel, BorderLayout.NORTH);

        // 2. Таблиця
        String[] columns = {"ID", "Дата", "Клієнт", "Послуга", "Фотограф", "Статус", "Ціна"};
        // Робимо клітинки нередагованими
        orderTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(orderTableModel);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        // Налаштуємо ширину першої колонки (ID), щоб не займала багато місця
        table.getColumnModel().getColumn(0).setPreferredWidth(60);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // 3. Нижня панель дій (Реалізація ВВ2)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton payBtn = new JButton("Прийняти оплату / Видати фото");
        payBtn.setBackground(new Color(255, 165, 0)); // Помаранчевий
        payBtn.setForeground(Color.BLACK);
        payBtn.setFont(new Font("Arial", Font.BOLD, 12));

        payBtn.addActionListener(e -> processPayment(table));

        actionPanel.add(payBtn);
        panel.add(actionPanel, BorderLayout.SOUTH);

        refreshOrderTable(); // Заповнити при старті
        return panel;
    }

    // --- ЕКРАН 3: КЛІЄНТИ ---
    private JPanel createClientsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 1. Заголовок і Кнопка "Додати"
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel title = new JLabel("База клієнтів");
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JButton addBtn = new JButton("Додати клієнта");
        addBtn.setBackground(new Color(70, 130, 180)); // Синій колір
        addBtn.setForeground(Color.BLACK); // Як ви просили
        addBtn.addActionListener(e -> showAddClientDialog());

        topPanel.add(title);
        topPanel.add(Box.createHorizontalStrut(20)); // Відступ
        topPanel.add(addBtn);

        panel.add(topPanel, BorderLayout.NORTH);

        // 2. Таблиця клієнтів
        String[] columns = {"ID", "Ім'я", "Телефон", "Email", "Статус"};
        clientTableModel = new javax.swing.table.DefaultTableModel(columns, 0);
        JTable table = new JTable(clientTableModel);

        // Налаштування таблиці (висота рядків, шрифт)
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Заповнюємо таблицю даними при створенні
        refreshClientTable();

        return panel;
    }

    // --- ЕКРАН 4: ЗВІТИ (АНАЛІТИКА) ---
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 1. Заголовок
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel title = new JLabel("Аналітика та Звіти (6 Запитів)");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(title);
        panel.add(topPanel, BorderLayout.NORTH);

        // 2. Центральна частина (Розділена: Кнопки зліва, Текст справа)
        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(300); // Ширина панелі кнопок
        splitPane.setDividerSize(5);

        // --- ЛІВА ЧАСТИНА (КНОПКИ) ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 1, 5, 5)); // 6 кнопок у стовпчик
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton btn1 = createReportButton("1. Кількість активних замовлень");
        btn1.addActionListener(e -> reportActiveOrders());

        JButton btn2 = createReportButton("2. Статистика клієнтів");
        btn2.addActionListener(e -> reportClients());

        JButton btn3 = createReportButton("3. Фотографи та слоти");
        btn3.addActionListener(e -> reportPhotographers());

        JButton btn4 = createReportButton("4. Список фото (по ID)");
        btn4.addActionListener(e -> reportPhotos());

        JButton btn5 = createReportButton("5. Загальна вартість (Дохід)");
        btn5.addActionListener(e -> reportRevenue());

        JButton btn6 = createReportButton("6. Популярна послуга");
        btn6.addActionListener(e -> reportPopularType());

        buttonPanel.add(btn1);
        buttonPanel.add(btn2);
        buttonPanel.add(btn3);
        buttonPanel.add(btn4);
        buttonPanel.add(btn5);
        buttonPanel.add(btn6);

        // --- ПРАВА ЧАСТИНА (РЕЗУЛЬТАТ) ---
        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        reportArea.setMargin(new Insets(10, 10, 10, 10));
        reportArea.setText("Оберіть запит зліва, щоб побачити результат...");

        splitPane.setLeftComponent(buttonPanel);
        splitPane.setRightComponent(new JScrollPane(reportArea));

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    // Тимчасова заглушка для екранів, які ми ще не зробили
    private JPanel createPlaceholderPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(title, JLabel.CENTER);
        label.setFont(new Font("Arial", Font.ITALIC, 24));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    // --- ЛОГІКА ---

    private void openOrderDialog() {
        // Перевірка: чи є хоч один фотограф?
        if (dataManager.getPhotographers().isEmpty()) {
            // Якщо немає - пропонуємо створити автоматично для тесту
            int res = JOptionPane.showConfirmDialog(this,
                    "У базі немає фотографів. Створити тестового фотографа?",
                    "Увага", JOptionPane.YES_NO_OPTION);

            if (res == JOptionPane.YES_OPTION) {
                dataManager.addPhotographer(new Photographer("Дмитро Шевченко", "0991234567", "Універсал"));
            } else {
                return;
            }
        }

        // Відкриваємо наше нове вікно
        OrderDialog dialog = new OrderDialog(this, dataManager);
        dialog.setVisible(true);

        // Якщо замовлення створено - можна оновити статистику на Dashboard (якщо ми її додамо)
        if (dialog.isSucceeded()) {
            refreshOrderTable();
            cardLayout.show(contentPanel, "ORDERS");
            System.out.println("Замовлення створено, оновлюємо інтерфейс...");
        }
    }

    private void saveData() {
        try {
            dataManager.saveDataToFile(DATA_DIR_PATH);
            JOptionPane.showMessageDialog(this, "Дані успішно збережено!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Помилка збереження: " + e.getMessage());
        }
    }

    private void loadData() {
        try {
            dataManager.loadDataFromFile(DATA_DIR_PATH);
        } catch (IOException e) {
            System.out.println("Файли не знайдено (перший запуск).");
        }
    }

    // Оновлення таблиці замовлень
    private void refreshOrderTable() {
        if (orderTableModel == null) return;

        orderTableModel.setRowCount(0); // Очистити

        // Форматер дати (щоб було красиво)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Order o : dataManager.getOrders()) {
            Object[] row = {
                    o.getId().substring(0, 6), // ID (короткий)
                    o.getOrderDate().format(formatter),
                    o.getClient().getName(),
                    o.getSessionType().getName(),
                    o.getPhotographer().getName(),
                    o.getStatus(), // NEW, PAID, etc.
                    o.getTotalCost() + " грн"
            };
            orderTableModel.addRow(row);
        }
    }

    // Логіка ВВ2: Прийом оплати
    private void processPayment(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Оберіть замовлення зі списку!", "Помилка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Отримуємо повний ID.
        // Увага: в таблиці ми показуємо скорочений ID, тому шукати треба обережно.
        // Але оскільки порядок рядків у таблиці співпадає зі списком (зазвичай),
        // надійніше знайти об'єкт за індексом, або зберігати повний ID у прихованій колонці.
        // Для спрощення в курсовій: знайдемо об'єкт у списку за індексом рядка (якщо не було сортування).

        Order selectedOrder = dataManager.getOrders().get(selectedRow);

        // Перевірка статусу
        if (selectedOrder.getStatus() == OrderStatus.PAID) {
            JOptionPane.showMessageDialog(this, "Це замовлення вже оплачено!", "Інфо", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Діалог підтвердження
        int confirm = JOptionPane.showConfirmDialog(this,
                "Прийняти оплату " + selectedOrder.getTotalCost() + " грн за замовлення " + selectedOrder.getId().substring(0,6) + "?\n" +
                        "Це змінить статус на PAID і створить чек.",
                "Оплата", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // 1. Змінюємо статус
            selectedOrder.setStatus(com.example.util.OrderStatus.PAID);

            // 2. Створюємо об'єкт Payment (як вимагає аналіз)
            Payment payment = new com.example.entity.Payment(
                    selectedOrder.getId(), selectedOrder.getTotalCost()
            );
            // (Тут можна додати payment в DataManager, якщо буде список платежів)

            // 3. Оновлюємо таблицю
            refreshOrderTable();
            JOptionPane.showMessageDialog(this, "Оплата успішна! Фото видано клієнту.");
        }
    }

    // Оновлює таблицю даними з DataManager
    private void refreshClientTable() {
        if (clientTableModel == null) return;

        clientTableModel.setRowCount(0); // Очистити таблицю

        for (Client c : dataManager.getClients()) {
            Object[] row = {
                    c.getId().substring(0, 8) + "...", // Скорочений ID для краси
                    c.getName(),
                    c.getPhoneNumber(),
                    c.getEmail(),
                    c.isRegular() ? "Постійний" : "Новий"
            };
            clientTableModel.addRow(row);
        }
    }

    // Показує просте діалогове вікно для додавання клієнта
    private void showAddClientDialog() {
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();

        Object[] message = {
                "ПІБ:", nameField,
                "Телефон:", phoneField,
                "Email:", emailField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Новий клієнт", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String phone = phoneField.getText();

            if (!name.isEmpty() && !phone.isEmpty()) {
                // Створюємо та зберігаємо
                Client newClient = new com.example.entity.Client(
                        name, phone, emailField.getText(), false
                );
                dataManager.addClient(newClient);

                // Оновлюємо вигляд
                refreshClientTable();
                JOptionPane.showMessageDialog(this, "Клієнт успішно доданий!");
            } else {
                JOptionPane.showMessageDialog(this, "Ім'я та телефон обов'язкові!", "Помилка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Допоміжний метод для створення красивих кнопок
    private JButton createReportButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setBackground(new Color(230, 230, 240));
        btn.setForeground(Color.BLACK);
        return btn;
    }

    // --- ЛОГІКА ЗВІТІВ ---

    private void reportActiveOrders() {
        long count = dataManager.getActiveOrdersCount();
        StringBuilder sb = new StringBuilder();
        sb.append("=== ЗАПИТ 1: АКТИВНІ ЗАМОВЛЕННЯ ===\n\n");
        sb.append("Кількість замовлень в роботі (NEW/IN_PROGRESS): ").append(count).append("\n\n");
        sb.append("Деталі:\n");

        dataManager.getOrders().stream()
                .filter(o -> o.getStatus() == OrderStatus.NEW || o.getStatus() == OrderStatus.IN_PROGRESS)
                .forEach(o -> sb.append("- ID: ").append(o.getId().substring(0,8))
                        .append(" | ").append(o.getClient().getName())
                        .append(" | ").append(o.getStatus()).append("\n"));

        reportArea.setText(sb.toString());
    }

    private void reportClients() {
        long regular = dataManager.getRegularClientsCount();
        long newCl = dataManager.getNewClientsCount();

        StringBuilder sb = new StringBuilder();
        sb.append("=== ЗАПИТ 2: СТАТИСТИКА КЛІЄНТІВ ===\n\n");
        sb.append("Всього клієнтів: ").append(regular + newCl).append("\n");
        sb.append("----------------------------\n");
        sb.append("Постійних клієнтів: ").append(regular).append("\n");
        sb.append("Нових клієнтів:     ").append(newCl).append("\n");

        reportArea.setText(sb.toString());
    }

    private void reportPhotographers() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ЗАПИТ 3: ФОТОГРАФИ ТА СЛОТИ ===\n\n");
        sb.append("Кількість фотографів: ").append(dataManager.getPhotographersCount()).append("\n\n");

        for (Photographer p : dataManager.getPhotographers()) {
            sb.append("Фотограф: ").append(p.getName()).append(" (").append(p.getSpecialization()).append(")\n");
            // Тут можна викликати метод p.getSchedule().checkAvailability(...) якби він був реалізований детально
            sb.append("  > Розклад активний\n");
            sb.append("\n");
        }
        reportArea.setText(sb.toString());
    }

    private void reportPhotos() {
        // Запитуємо ID замовлення у користувача
        String orderId = JOptionPane.showInputDialog(this, "Введіть ID замовлення (або його частину):");
        if (orderId == null || orderId.trim().isEmpty()) return;

        // Шукаємо замовлення
       Order foundOrder = dataManager.getOrders().stream()
                .filter(o -> o.getId().startsWith(orderId)) // Дозволяємо вводити неповний ID
                .findFirst().orElse(null);

        StringBuilder sb = new StringBuilder();
        sb.append("=== ЗАПИТ 4: СПИСОК ФОТО ===\n\n");

        if (foundOrder != null) {
            List<Photo> photos = dataManager.getPhotosForOrder(foundOrder.getId());
            sb.append("Замовлення знайдено: ").append(foundOrder.getId()).append("\n");
            sb.append("Клієнт: ").append(foundOrder.getClient().getName()).append("\n");
            sb.append("Кількість фото: ").append(photos.size()).append("\n\n");

            if (photos.isEmpty()) {
                sb.append("(Фотографії ще не завантажені)");
            } else {
                photos.forEach(p -> sb.append(" -> Файл: ").append(p.getFilePath()).append("\n"));
            }
        } else {
            sb.append("Замовлення з таким ID не знайдено.");
        }
        reportArea.setText(sb.toString());
    }

    private void reportRevenue() {
        // Для курсової беремо "За весь час"
        java.time.LocalDateTime start = java.time.LocalDateTime.MIN;
        java.time.LocalDateTime end = java.time.LocalDateTime.MAX;

        double total = dataManager.getTotalRevenueForPeriod(start, end);

        StringBuilder sb = new StringBuilder();
        sb.append("=== ЗАПИТ 5: ФІНАНСОВИЙ ЗВІТ ===\n\n");
        sb.append("Період: За весь час\n");
        sb.append("----------------------------\n");
        sb.append("ЗАГАЛЬНИЙ ДОХІД: ").append(total).append(" грн\n");
        sb.append("(Сума вартості всіх замовлень)");

        reportArea.setText(sb.toString());
    }

    private void reportPopularType() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ЗАПИТ 6: ПОПУЛЯРНА ПОСЛУГА ===\n\n");

        String popular = dataManager.getMostPopularSessionType().orElse("Немає даних");

        sb.append("Тип фотосесії з найбільшим попитом:\n");
        sb.append(">>> ").append(popular).append(" <<<\n");

        reportArea.setText(sb.toString());
    }
}