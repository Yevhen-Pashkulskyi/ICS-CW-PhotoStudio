package com.example.ui.panels;

import com.example.control.DataManager;
import com.example.entity.Photographer;
import com.example.model.Order;
import com.example.util.OrderStatus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Панель графічного інтерфейсу для модуля аналітики та звітності.
 * <p>
 * Цей клас відповідає за реалізацію <b>IV Етапу</b> курсової роботи (виконання 6-ти запитів).
 * Інтерфейс побудовано за принципом "Master-Detail": зліва знаходиться меню вибору звіту,
 * справа — текстова область для виведення результатів.
 */
public class ReportsPanel extends JPanel {

    /** Посилання на контролер даних для отримання статистики. */
    private DataManager dataManager;

    /** Текстова область для відображення згенерованих звітів. */
    private JTextArea reportArea;

    /**
     * Конструктор панелі звітів.
     * Налаштовує розділений екран (JSplitPane), створює кнопки для кожного типу звіту
     * та прив'язує їх до відповідних методів обробки.
     *
     * @param dataManager екземпляр менеджера даних.
     */
    public ReportsPanel(DataManager dataManager) {
        this.dataManager = dataManager;
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
        sb.append("Кількість: ").append(dataManager.getActiveOrdersCount()).append("\n");

        // Використання Stream API для фільтрації та форматування
        dataManager.getOrders().stream()
                .filter(o -> o.getStatus() == OrderStatus.NEW || o.getStatus() == OrderStatus.IN_PROGRESS)
                .forEach(o -> sb.append(o.getId().substring(0,8)).append(" - ").append(o.getStatus()).append("\n"));

        reportArea.setText(sb.toString());
    }

    /**
     * Реалізація Запиту №2: Статистика клієнтів.
     * Порівнює кількість нових та постійних клієнтів.
     */
    private void reportClients() {
        reportArea.setText("Постійних: " + dataManager.getRegularClientsCount() +
                "\nНових: " + dataManager.getNewClientsCount());
    }

    /**
     * Реалізація Запиту №3: Кількість фотографів.
     * Виводить список персоналу та їх спеціалізацію.
     */
    private void reportPhotographers() {
        LocalDateTime now = LocalDateTime.now();

        // Отримуємо список тих, хто вільний прямо зараз
        java.util.List<Photographer> freePhotographers = dataManager.getAvailablePhotographers(now);

        StringBuilder sb = new StringBuilder();
        sb.append("=== ЗАПИТ 3: ФОТОГРАФИ ТА СТАТУС ===\n");
        sb.append("Станом на: ").append(now.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))).append("\n");
        sb.append("Всього фотографів: ").append(dataManager.getPhotographersCount()).append("\n\n");

        for (Photographer p : dataManager.getPhotographers()) {
            sb.append("• ").append(p.getName()).append(" (").append(p.getSpecialization()).append(")");

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
        String id = JOptionPane.showInputDialog(this, "ID замовлення:");
        if (id == null) return; // Користувач натиснув Cancel

        // Пошук замовлення (підтримується введення неповного ID)
        Order order = dataManager.getOrders().stream()
                .filter(o -> o.getId().startsWith(id))
                .findFirst().orElse(null);

        if (order != null) {
            StringBuilder sb = new StringBuilder("Фото для ").append(order.getId()).append("\n");
            dataManager.getPhotosForOrder(order.getId()).forEach(p -> sb.append(p.getFilePath()).append("\n"));
            reportArea.setText(sb.toString());
        } else {
            reportArea.setText("Замовлення не знайдено.");
        }
    }

    /**
     * Реалізація Запиту №5: Загальна вартість усіх замовлень.
     * Розраховує сумарний дохід за весь період існування системи.
     */
    private void reportRevenue() {
        reportArea.setText("Загальний дохід: " +
                dataManager.getTotalRevenueForPeriod(LocalDateTime.MIN, LocalDateTime.MAX) + " грн");
    }

    /**
     * Реалізація Запиту №6: Тип фотосесії з найбільшим попитом.
     * Аналізує історію замовлень та визначає найпопулярнішу послугу.
     */
    private void reportPopularType() {
        reportArea.setText("Популярна: " + dataManager.getMostPopularSessionType().orElse("-"));
    }
}