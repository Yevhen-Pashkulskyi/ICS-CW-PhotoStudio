package com.example.ui.panels;

import com.example.control.DataManager;
import com.example.entity.Payment;
import com.example.model.Order;
import com.example.util.OrderStatus;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * Панель графічного інтерфейсу для управління списком замовлень.
 * Забезпечує відображення історії замовлень у табличному вигляді
 * та надає функціонал для зміни статусу замовлення (прийом оплати).
 * Є реалізацією вкладки "Замовлення" у головному вікні.
 */
public class OrdersPanel extends JPanel {

    /** Посилання на центральний контролер даних. */
    private DataManager dataManager;

    /** Модель таблиці, що зберігає дані про замовлення для відображення. */
    private DefaultTableModel orderTableModel;

    /**
     * Конструктор панелі замовлень.
     * Налаштовує макет (Layout), створює таблицю з нередагованими клітинками
     * та панель інструментів для дій над замовленнями.
     *
     * @param dataManager екземпляр менеджера даних.
     */
    public OrdersPanel(DataManager dataManager) {
        this.dataManager = dataManager;
        setLayout(new BorderLayout());

        // Верхня панель: Заголовок та кнопка оновлення
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel title = new JLabel("Управління замовленнями");
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JButton refreshBtn = new JButton("Оновити список");
        refreshBtn.addActionListener(e -> refreshTable());

        topPanel.add(title);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(refreshBtn);
        add(topPanel, BorderLayout.NORTH);

        // Налаштування таблиці
        String[] columns = {"ID", "Дата", "Клієнт", "Послуга", "Фотограф", "Статус", "Ціна"};

        // Перевизначення моделі для заборони редагування клітинок вручну
        orderTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(orderTableModel);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 13));

        // Встановлення ширини першої колонки (ID), щоб заощадити місце
        table.getColumnModel().getColumn(0).setPreferredWidth(60);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Нижня панель дій (Сценарій ВВ2: Оплата)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton payBtn = new JButton("Прийняти оплату / Видати фото");
        payBtn.setBackground(new Color(255, 165, 0)); // Помаранчевий колір для акценту
        payBtn.setForeground(Color.BLACK);
        payBtn.setFont(new Font("Arial", Font.BOLD, 12));

        payBtn.addActionListener(e -> processPayment(table));

        actionPanel.add(payBtn);
        add(actionPanel, BorderLayout.SOUTH);

        // Завантаження даних при ініціалізації
        refreshTable();
    }

    /**
     * Оновлює дані в таблиці замовлень.
     * Очищує поточний вміст моделі та заново наповнює її даними з DataManager.
     * Використовує {@link DateTimeFormatter} для зручного відображення дати створення.
     */
    public void refreshTable() {
        orderTableModel.setRowCount(0); // Очищення таблиці
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Order o : dataManager.getOrders()) {
            Object[] row = {
                    o.getId().substring(0, 6), // Скорочений ID
                    o.getOrderDate().format(formatter),
                    o.getClient().getName(),
                    o.getSessionType().getName(),
                    o.getPhotographer().getName(),
                    o.getStatus(),
                    o.getTotalCost() + " грн"
            };
            orderTableModel.addRow(row);
        }
    }

    /**
     * Обробляє процес оплати обраного замовлення (Реалізація Сценарію ВВ2).
     * <p>
     * Алгоритм роботи:
     * <ol>
     * <li>Перевіряє, чи обрано рядок у таблиці.</li>
     * <li>Перевіряє поточний статус (неможливо оплатити вже оплачене замовлення).</li>
     * <li>Запитує підтвердження у користувача.</li>
     * <li>Змінює статус замовлення на {@code PAID}.</li>
     * <li>Створює запис про платіж {@link Payment}.</li>
     * <li>Викликає перевірку лояльності клієнта (чи став він постійним).</li>
     * <li>Оновлює таблицю та виводить повідомлення про результат.</li>
     * </ol>
     *
     * @param table посилання на таблицю для визначення обраного рядка.
     */
    private void processPayment(JTable table) {
        int selectedRow = table.getSelectedRow();

        // Валідація вибору
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Оберіть замовлення!", "Помилка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Отримання об'єкта замовлення зі списку (за індексом рядка)
        Order selectedOrder = dataManager.getOrders().get(selectedRow);

        // Перевірка бізнес-правила: не можна платити двічі
        if (selectedOrder.getStatus() == OrderStatus.PAID) {
            JOptionPane.showMessageDialog(this, "Вже оплачено!", "Інфо", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Прийняти оплату " + selectedOrder.getTotalCost() + " грн?", "Оплата", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // 1. Ставимо статус ОПЛАЧЕНО
            selectedOrder.setStatus(OrderStatus.PAID);

            // 2. Фіксуємо факт платежу (створення об'єкта)
            new Payment(selectedOrder.getId(), selectedOrder.getTotalCost());

            // 3. === БІЗНЕС-ЛОГІКА: Перевірка на підвищення статусу клієнта ===
            dataManager.checkAndUpgradeClient(selectedOrder.getClient());

            // 4. Оновлюємо інтерфейс
            refreshTable();

            // Інформування користувача про результат
            if (selectedOrder.getClient().isRegular()) {
                JOptionPane.showMessageDialog(this,
                        "Оплата успішна!\nУВАГА: Цей клієнт досяг 3-х замовлень і отримав статус 'Постійний'!");
            } else {
                JOptionPane.showMessageDialog(this, "Оплата успішна!");
            }
        }
    }
}