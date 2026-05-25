package com.example.ui.panels;

import com.example.control.OrderController;
import com.example.entity.Photo;
import com.example.entity.Photographer;
import com.example.model.Order;
import com.example.util.OrderStatus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
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
        splitPane.setDividerLocation(300); // Ширина меню кнопок

        // --- Панель кнопок (Лівa частина) ---
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Використання посилань на методи (Method References) для чистоти коду
        addButton(buttonPanel, "1. Активні замовлення", this::reportActiveOrders);
        addButton(buttonPanel, "2. Статистика клієнтів", this::reportClients);
        addButton(buttonPanel, "3. Фотографи", this::reportPhotographers);
        addButton(buttonPanel, "4. Список фото (по ID)", this::reportPhotos);
        addButton(buttonPanel, "5. Дохід", this::reportRevenue);
        addButton(buttonPanel, "6. Популярна послуга", this::reportPopularType);

        // --- Область виводу (Права частина) ---
        reportArea = new JTextArea();
        reportArea.setEditable(false); // Заборона редагування користувачем
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); // Моноширинний шрифт для вирівнювання

        splitPane.setLeftComponent(buttonPanel);
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
        btn.addActionListener(e -> action.run());
        panel.add(btn);
    }

    // --- Логіка генерації звітів ---

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