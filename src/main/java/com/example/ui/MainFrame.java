package com.example.ui;

import com.example.control.DataManager;
import com.example.entity.Client;
import com.example.entity.Photo;
import com.example.entity.Photographer;
import com.example.model.Order;
import com.example.service.SessionType;
import com.example.util.OrderStatus;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class MainFrame extends JFrame {

    private DataManager dataManager;
    // Шлях "." означає поточну папку проєкту
    private static final String DATA_DIR_PATH = ".";

    private DefaultListModel<Client> clientListModel;
    private JTextArea reportArea; // Поле для виводу результатів запитів

    public MainFrame() {
        setTitle("Фотоательє - Курсова робота");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        this.dataManager = new DataManager();
        this.clientListModel = new DefaultListModel<>();

        // Спроба завантажити дані при старті
        loadData();

        // Створення вкладок
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Клієнти", createClientsPanel());
        tabbedPane.addTab("Звіти та Аналітика (6 Запитів)", createReportsPanel());
        tabbedPane.addTab("Управління", createAdminPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Меню для збереження файлів
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Файл");

        JMenuItem saveItem = new JMenuItem("Зберегти дані у CSV");
        saveItem.addActionListener(e -> saveData());

        JMenuItem loadItem = new JMenuItem("Перезавантажити дані");
        loadItem.addActionListener(e -> loadData());

        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    // --- 1. Вкладка "Клієнти" ---
    private JPanel createClientsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JList<Client> clientList = new JList<>(clientListModel);
        panel.add(new JScrollPane(clientList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addClientButton = new JButton("Додати нового клієнта");

        addClientButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Введіть ім'я клієнта:");
            if (name != null && !name.trim().isEmpty()) {
                String phone = JOptionPane.showInputDialog(this, "Телефон:");
                String email = JOptionPane.showInputDialog(this, "Email:");
                // За замовчуванням додаємо як "Нового" (false)
                Client newClient = new Client(name, phone, email, false);

                dataManager.addClient(newClient);
                clientListModel.addElement(newClient);
            }
        });

        buttonPanel.add(addClientButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    // --- 2. Вкладка "Звіти" (РЕАЛІЗАЦІЯ 6 ЗАПИТІВ) ---
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Область для виводу тексту
        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);

        // Панель з кнопками запитів
        JPanel buttonsGrid = new JPanel(new GridLayout(3, 2, 10, 10));
        buttonsGrid.setBorder(BorderFactory.createTitledBorder("Запити до системи"));

        // Кнопка 1: Активні замовлення
        JButton btnActiveOrders = new JButton("1. Кількість активних замовлень");
        btnActiveOrders.addActionListener(e -> {
            long count = dataManager.getActiveOrdersCount();
            reportArea.setText("--- ЗАПИТ 1 ---\n");
            reportArea.append("Кількість активних замовлень (New/In Progress): " + count + "\n");
            // Виведемо деталі для наочності
            dataManager.getOrders().forEach(o -> reportArea.append(o.toString() + "\n"));
        });

        // Кнопка 2: Клієнти
        JButton btnClientsCount = new JButton("2. Статистика клієнтів");
        btnClientsCount.addActionListener(e -> {
            long regular = dataManager.getRegularClientsCount();
            long newClients = dataManager.getNewClientsCount();
            reportArea.setText("--- ЗАПИТ 2 ---\n");
            reportArea.append("Постійних клієнтів: " + regular + "\n");
            reportArea.append("Нових клієнтів: " + newClients + "\n");
            reportArea.append("Всього: " + (regular + newClients));
        });

        // Кнопка 3: Фотографи
        JButton btnPhotographers = new JButton("3. Фотографи");
        btnPhotographers.addActionListener(e -> {
            int count = dataManager.getPhotographersCount();
            reportArea.setText("--- ЗАПИТ 3 ---\n");
            reportArea.append("Кількість фотографів у штаті: " + count + "\n");
            dataManager.getPhotographers().forEach(p ->
                    reportArea.append(" - " + p.getName() + " (" + p.getSpecialization() + ")\n"));
        });

        // Кнопка 4: Список фото (беремо перше замовлення для прикладу)
        JButton btnPhotos = new JButton("4. Фото для замовлення");
        btnPhotos.addActionListener(e -> {
            reportArea.setText("--- ЗАПИТ 4 ---\n");
            // Для прикладу беремо перше замовлення зі списку
            if (!dataManager.getOrders().isEmpty()) {
                Order order = dataManager.getOrders().get(0);
                List<Photo> photos = dataManager.getPhotosForOrder(order.getId());
                reportArea.append("Замовлення: " + order.getId() + "\n");
                reportArea.append("Знайдено фото: " + photos.size() + "\n");
                photos.forEach(p -> reportArea.append(" -> " + p.getFilePath() + "\n"));
            } else {
                reportArea.append("Немає замовлень для перевірки.");
            }
        });

        // Кнопка 5: Вартість за період (за весь час для тесту)
        JButton btnRevenue = new JButton("5. Загальна вартість (Дохід)");
        btnRevenue.addActionListener(e -> {
            // Беремо широкий діапазон дат для демонстрації
            LocalDateTime start = LocalDateTime.now().minusYears(1);
            LocalDateTime end = LocalDateTime.now().plusYears(1);
            double total = dataManager.getTotalRevenueForPeriod(start, end);

            reportArea.setText("--- ЗАПИТ 5 ---\n");
            reportArea.append("Період: Останній рік\n");
            reportArea.append("Загальна вартість замовлень: " + total + " грн");
        });

        // Кнопка 6: Популярна фотосесія
        JButton btnPopular = new JButton("6. Найпопулярніша послуга");
        btnPopular.addActionListener(e -> {
            reportArea.setText("--- ЗАПИТ 6 ---\n");
            String popular = dataManager.getMostPopularSessionType().orElse("Немає даних");
            reportArea.append("Тип фотосесії з найбільшим попитом: \n" + popular);
        });

        buttonsGrid.add(btnActiveOrders);
        buttonsGrid.add(btnClientsCount);
        buttonsGrid.add(btnPhotographers);
        buttonsGrid.add(btnPhotos);
        buttonsGrid.add(btnRevenue);
        buttonsGrid.add(btnPopular);

        panel.add(buttonsGrid, BorderLayout.SOUTH);
        return panel;
    }

    // --- 3. Вкладка "Управління" (Генерація даних) ---
    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        JButton generateBtn = new JButton("Згенерувати тестові дані");

        generateBtn.addActionListener(e -> {
            generateTestData();
            JOptionPane.showMessageDialog(this, "Дані згенеровано! Перейдіть на вкладку 'Звіти' або 'Клієнти'.");
        });

        panel.add(generateBtn);
        return panel;
    }

    // --- Допоміжні методи ---

    private void saveData() {
        try {
            dataManager.saveDataToFile(DATA_DIR_PATH);
            JOptionPane.showMessageDialog(this, "Дані збережено у файли CSV!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Помилка: " + e.getMessage());
        }
    }

    private void loadData() {
        try {
            dataManager.loadDataFromFile(DATA_DIR_PATH);
            // Оновлення UI списку клієнтів
            clientListModel.clear();
            for (Client c : dataManager.getClients()) {
                clientListModel.addElement(c);
            }
            if (reportArea != null) reportArea.setText("Дані завантажено!\nЗамовлень: " + dataManager.getOrders().size());
        } catch (IOException e) {
            System.out.println("Перший запуск або файли відсутні.");
        }
    }

    // Метод для створення фейкових даних, щоб перевірити звіти
    private void generateTestData() {
        // 1. Клієнти
        Client c1 = new Client("Іван Петров", "0501112233", "ivan@mail.com", true); // Постійний
        Client c2 = new Client("Олена Сидорова", "0972223344", "olena@mail.com", false);
        Client c3 = new Client("Андрій Коваль", "0633334455", "andriy@mail.com", false);

        dataManager.addClient(c1);
        dataManager.addClient(c2);
        dataManager.addClient(c3);
        clientListModel.addElement(c1);
        clientListModel.addElement(c2);
        clientListModel.addElement(c3);

        // 2. Фотографи
        Photographer p1 = new Photographer("Марія Арт", "0509998877", "Портрет");
        Photographer p2 = new Photographer("Дмитро Лінза", "0678887766", "Весілля");
        dataManager.addPhotographer(p1);
        dataManager.addPhotographer(p2);

        // 3. Замовлення
        // Замовлення 1: Портрет для Івана (Постійний клієнт)
        Order o1 = new Order(c1, p1, new SessionType("Портрет", 1000));
        o1.setStatus(OrderStatus.COMPLETED); // Активне? Ні, завершене, але не оплачене
        o1.getPhotos().add(new Photo("C:/photos/img1.jpg"));
        o1.getPhotos().add(new Photo("C:/photos/img2.jpg"));

        // Замовлення 2: Весілля для Олени
        Order o2 = new Order(c2, p2, new SessionType("Весілля", 5000));
        o2.setStatus(OrderStatus.NEW); // Активне

        // Замовлення 3: Портрет для Андрія
        Order o3 = new Order(c3, p1, new SessionType("Портрет", 1000));
        o3.setStatus(OrderStatus.IN_PROGRESS); // Активне

        dataManager.addOrder(o1);
        dataManager.addOrder(o2);
        dataManager.addOrder(o3);
    }
}