package com.example.ui.panels;

import com.example.control.OrderController;
import com.example.dataDB.storage.ReportDAO;
import com.example.entity.Photo;
import com.example.entity.Photographer;
import com.example.entity.Order;
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
 */
public class ReportsPanel extends JPanel {

    private final OrderController orderController;
    private final JTextArea reportArea;

    public ReportsPanel(OrderController orderController) {
        this.orderController = orderController;
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel title = new JLabel("Аналітика та Звіти");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(title);
        add(topPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(320);

        // --- ЛІВА ЧАСТИНА: Меню категорій звітів ---
        JPanel menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel labelLocal = new JLabel("Оперативний аналіз додатка:");
        labelLocal.setFont(new Font("Arial", Font.BOLD, 13));
        labelLocal.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuContainer.add(labelLocal);
        menuContainer.add(Box.createVerticalStrut(5));

        JPanel localButtonsPanel = new JPanel(new GridLayout(6, 1, 4, 4));
        localButtonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        addButton(localButtonsPanel, "Активні замовлення", this::reportActiveOrders);
        addButton(localButtonsPanel, "Статистика клієнтів", this::reportClients);
        addButton(localButtonsPanel, "Фотографи", this::reportPhotographers);
        addButton(localButtonsPanel, "Список фото (по ID)", this::reportPhotos);
        addButton(localButtonsPanel, "Дохід", this::reportRevenue);
        addButton(localButtonsPanel, "Популярна послуга", this::reportPopularType);
        menuContainer.add(localButtonsPanel);

        menuContainer.add(Box.createVerticalStrut(20));

        JLabel labelDb = new JLabel("🗄️ Глибока SQL-аналітика бази даних:");
        labelDb.setFont(new Font("Arial", Font.BOLD, 13));
        labelDb.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuContainer.add(labelDb);
        menuContainer.add(Box.createVerticalStrut(5));

        JPanel dbButtonsPanel = new JPanel(new GridLayout(9, 1, 4, 4));
        dbButtonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        addButton(dbButtonsPanel, "Клієнти заданого фотографа", this::sqlClientsByPhotographer);
        addButton(dbButtonsPanel, "Пошук клієнтів на літеру", this::sqlClientsByLetter);
        addButton(dbButtonsPanel, "Замовлення за період", this::sqlOrdersInPeriod);
        addButton(dbButtonsPanel, "Кількість нових за тиждень", this::sqlNewOrdersWeekCount);
        addButton(dbButtonsPanel, "Кількість замовлень кожного", this::sqlOrdersCountPerPhotographer);
        addButton(dbButtonsPanel, "Максимум замовлень", this::sqlMostLoadedPhotographers);
        addButton(dbButtonsPanel, "Топ-ставки по категоріях", this::sqlTopRatesBySpec);
        addButton(dbButtonsPanel, "Вільні фотографи на Травень", this::sqlNoOrdersInMay);
        addButton(dbButtonsPanel, "Аналіз завантаженості", this::sqlLoadingStatusUnion);
        menuContainer.add(dbButtonsPanel);

        JScrollPane menuScrollPane = new JScrollPane(menuContainer);
        menuScrollPane.setBorder(null);

        // --- ПРАВА ЧАСТИНА: Область виводу моноширинного тексту ---
        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

        splitPane.setLeftComponent(menuScrollPane);
        splitPane.setRightComponent(new JScrollPane(reportArea));
        add(splitPane, BorderLayout.CENTER);
    }

    private void addButton(JPanel panel, String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        btn.addActionListener(e -> action.run());
        panel.add(btn);
    }

    /**
     * Форматує масив даних String[] у рівну текстову реляційну таблицю.
     */
    private void printTableReport(String reportName, String[] headers, List<String[]> rows) {
        StringBuilder sb = new StringBuilder("=== SQL ЗВІТ: " + reportName.toUpperCase() + " ===\n\n");

        for (String header : headers) {
            sb.append(String.format("%-30s | ", header));
        }
        sb.append("\n").append("-".repeat(headers.length * 33)).append("\n");

        if (rows.isEmpty()) {
            sb.append("[ База даних повернула порожній результат. Дані для аналізу відсутні ]\n");
        } else {
            for (String[] row : rows) {
                for (String cell : row) {
                    sb.append(String.format("%-30s | ", cell != null ? cell : "NULL"));
                }
                sb.append("\n");
            }
        }
        reportArea.setText(sb.toString());
    }

    private ReportDAO getReportDAO() {
        return orderController.getDatabaseManager().getReportDAO();
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
            JOptionPane.showMessageDialog(this, "ID має бути числовим значенням!", "Помилка вводу", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            reportArea.setText("Помилка генерації SQL-звіту:\n" + e.getMessage());
        }
    }

    private void sqlClientsByLetter() {
        String letter = JOptionPane.showInputDialog(this, "З якої літери починається ПІБ клієнта:");
        if (letter == null || letter.trim().isEmpty()) return;
        try {
            List<String[]> data = getReportDAO().getClientsByLetter(letter.trim());
            printTableReport("Клієнти на літеру '" + letter + "'",
                    new String[]{"Повне Ім'я", "Телефон", "Email"}, data);
        } catch (Exception e) {
            reportArea.setText("Помилка генерації SQL-звіту:\n" + e.getMessage());
        }
    }

    private void sqlOrdersInPeriod() {
        try {
            Timestamp start = Timestamp.valueOf("2026-05-01 00:00:00");
            Timestamp end = Timestamp.valueOf("2026-05-31 23:59:59");
            List<String[]> data = getReportDAO().getOrdersInPeriod(start, end);
            printTableReport("Замовлення за Травень 2026 року",
                    new String[]{"ID Ордера", "ID Клієнта", "Дата події", "Вартість"}, data);
        } catch (Exception e) {
            reportArea.setText("Помилка генерації SQL-звіту:\n" + e.getMessage());
        }
    }

    private void sqlNewOrdersWeekCount() {
        try {
            int count = getReportDAO().getNewOrdersCountLastWeek();
            StringBuilder sb = new StringBuilder("=== SQL АГРЕГАТНИЙ ЗВІТ ===\n\n");
            sb.append("Завдання: Скільки нових замовлень надійшло за останні 7 днів?\n");
            sb.append("-----------------------------------------------------------------\n");
            sb.append("Результат лічильника: ").append(count).append(" нових звернень.\n");
            reportArea.setText(sb.toString());
        } catch (Exception e) {
            reportArea.setText("Помилка генерації SQL-звіту:\n" + e.getMessage());
        }
    }

    private void sqlOrdersCountPerPhotographer() {
        try {
            List<String[]> data = getReportDAO().getOrdersCountPerPhotographer();
            printTableReport("Кількість замовлень по персоналу",
                    new String[]{"Фотограф", "Всього замовлень (шт)"}, data);
        } catch (Exception e) {
            reportArea.setText("Помилка генерації SQL-звіту:\n" + e.getMessage());
        }
    }

    private void sqlMostLoadedPhotographers() {
        try {
            List<String[]> data = getReportDAO().getMostLoadedPhotographers();
            printTableReport("Майстри з максимальною кількістю замовлень (ALL)",
                    new String[]{"Ім'я кращого фотографа"}, data);
        } catch (Exception e) {
            reportArea.setText("Помилка генерації SQL-звіту:\n" + e.getMessage());
        }
    }

    private void sqlTopRatesBySpec() {
        try {
            List<String[]> data = getReportDAO().getTopPhotographersBySpecialization();
            printTableReport("Найвищі базові ставки за кожною спеціалізацією",
                    new String[]{"Спеціалізація", "Фотограф", "Ставка"}, data);
        } catch (Exception e) {
            reportArea.setText("Помилка генерації SQL-звіту:\n" + e.getMessage());
        }
    }

    private void sqlNoOrdersInMay() {
        try {
            List<String[]> data = getReportDAO().getPhotographersWithNoOrdersInMay2026();
            printTableReport("Фотоографи без замовлень на Травень 2026 (NOT EXISTS)",
                    new String[]{"Вільний персонал"}, data);
        } catch (Exception e) {
            reportArea.setText("Помилка генерації SQL-звіту:\n" + e.getMessage());
        }
    }

    private void sqlLoadingStatusUnion() {
        try {
            List<String[]> data = getReportDAO().getPhotographerLoadingStatus();
            printTableReport("Аналіз маркерів завантаженості кадрів (UNION)",
                    new String[]{"Фотограф", "Статус системи маркетингу"}, data);
        } catch (Exception e) {
            reportArea.setText("Помилка генерації SQL-звіту:\n" + e.getMessage());
        }
    }

    private void reportActiveOrders() {
        StringBuilder sb = new StringBuilder("=== АКТИВНІ ЗАМОВЛЕННЯ ===\n\n");
        sb.append("Кількість: ").append(orderController.getActiveOrdersCount()).append("\n");
        sb.append(String.format("%-10s | %-15s | %-20s\n", "ID", "Статус", "Клієнт"));
        sb.append("--------------------------------------------------\n");

        orderController.getOrders().stream()
                .filter(o -> o.getStatus() == OrderStatus.NEW || o.getStatus() == OrderStatus.IN_PROGRESS)
                .forEach(o -> sb.append(String.format("%-10d | %-15s | %-20s\n",
                        o.getId(), o.getStatus(), o.getClient() != null ? o.getClient().getFullName() : "Невідомо")));

        reportArea.setText(sb.toString());
    }

    private void reportClients() {
        StringBuilder sb = new StringBuilder("=== СТАТИСТИКА БАЗИ КЛІЄНТІВ ===\n\n");
        sb.append("Постоянні клієнти (мають знижку): ").append(orderController.getRegularClientsCount()).append("\n");
        sb.append("Нові клієнти (базовий тариф):   ").append(orderController.getNewClientsCount()).append("\n");
        sb.append("--------------------------------------------------\n");
        sb.append("Всього зареєстровано в базі:     ").append(orderController.getClients().size());
        reportArea.setText(sb.toString());
    }

    private void reportPhotographers() {
        LocalDateTime now = LocalDateTime.now();
        List<Photographer> freePhotographers = orderController.getAvailablePhotographers(now);

        StringBuilder sb = new StringBuilder("=== МОНІТОРИНГ ЗАЙНЯТОСТІ ФОТОГРАФІВ ===\n");
        sb.append("Станом на: ").append(now.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))).append("\n");
        sb.append("Всього фотографів: ").append(orderController.getPhotographers().size()).append("\n\n");

        for (Photographer p : orderController.getPhotographers()) {
            sb.append("• ").append(p.getFullName()).append(" (").append(p.getSpecialization()).append(")");

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

    private void reportPhotos() {
        String input = JOptionPane.showInputDialog(this, "ID замовлення:");
        if (input == null || input.trim().isEmpty()) return;
        try {
            long orderId = Long.parseLong(input);
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

    private void reportRevenue() {
        double totalRevenue = orderController.getTotalRevenue();
        StringBuilder sb = new StringBuilder("=== ФІНАНСОВИЙ АНАЛІТИЧНИЙ ЗВІТ ===\n\n");
        sb.append("Загальна каса фотостудії (всі оплачені ордери):\n");
        sb.append(String.format("%.2f грн\n", totalRevenue));
        reportArea.setText(sb.toString());
    }

    private void reportPopularType() {
        StringBuilder sb = new StringBuilder("=== МАРКЕТИНГОВИЙ АНАЛІЗ ПОПИТУ ===\n\n");
        sb.append("Найбільш затребуваний тип фотосесії:\n");
        sb.append("⭐ ").append(orderController.getMostPopularSessionType().orElse("Дані відсутні (немає замовлень)")).append("\n");
        reportArea.setText(sb.toString());
    }
}