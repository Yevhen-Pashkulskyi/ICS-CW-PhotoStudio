package com.example.ui.panels;

import com.example.control.OrderController;
import com.example.entity.Payment;
import com.example.entity.Order;
import com.example.util.OrderStatus;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

/**
 * Панель графічного інтерфейсу для управління списком замовлень.
 */
public class OrdersPanel extends JPanel {

    private final OrderController orderController;
    private final DefaultTableModel orderTableModel;

    public OrdersPanel(OrderController orderController) {
        this.orderController = orderController;
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

        // Налаштування таблиці (7 колонок)
        String[] columns = {"ID", "Дата", "Клієнт", "Послуга", "Фотограф", "Статус", "Ціна"};

        orderTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(orderTableModel);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getColumnModel().getColumn(0).setPreferredWidth(60);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Нижня панель дій
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton payBtn = new JButton("Прийняти оплату / Видати фото");
        payBtn.setBackground(new Color(255, 165, 0));
        payBtn.setForeground(Color.BLACK);
        payBtn.setFont(new Font("Arial", Font.BOLD, 12));

        payBtn.addActionListener(e -> processPayment(table));

        actionPanel.add(payBtn);
        add(actionPanel, BorderLayout.SOUTH);

        refreshTable();
    }

    public void refreshTable() {
        orderTableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Order o : orderController.getOrders()) {
            Timestamp createdDate = o.getCreatedDate();
            String formattedDate = (createdDate != null)
                    ? createdDate.toLocalDateTime().format(formatter)
                    : "Не вказано";

            Object[] row = {
                    o.getId(),
                    formattedDate,
                    o.getClient() != null ? o.getClient().getFullName() : "Видалений клієнт",
                    o.getSessionType() != null ? o.getSessionType().getSessionName() : "Невідома послуга",
                    o.getPhotographer() != null ? o.getPhotographer().getFullName() : "Без фотографа",
                    o.getStatus(),
                    String.format("%.2f грн", o.getTotalCost())
            };
            orderTableModel.addRow(row);
        }
    }

    private void processPayment(JTable table) {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Оберіть замовлення зі списку!", "Помилка вибору", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            long orderId = Long.parseLong(table.getValueAt(selectedRow, 0).toString());

            Order selectedOrder = orderController.getOrders().stream()
                    .filter(o -> o.getId() == orderId)
                    .findFirst()
                    .orElse(null);

            if (selectedOrder == null) {
                JOptionPane.showMessageDialog(this, "Замовлення не знайдено в системі!", "Помилка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (selectedOrder.getStatus() == OrderStatus.PAID) {
                JOptionPane.showMessageDialog(this, "Це замовлення вже успішно оплачене!", "Інформація", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    String.format("Прийняти оплату в розмірі %.2f грн?", selectedOrder.getTotalCost()),
                    "Підтвердження транзакції", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Делегуємо проведення оплати контролеру
                orderController.completeOrderPayment(selectedOrder);

                refreshTable(); // Синхронне оновлення UI

                if (selectedOrder.getClient() != null && selectedOrder.getClient().isRegular()) {
                    JOptionPane.showMessageDialog(this,
                            "Оплата успішна!\nУВАГА: Клієнт отримав статус 'Постійний' та постійну знижку!",
                            "Статус оновлено", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Оплата успішно проведена!", "Успіх", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Помилка при проведенні оплати: " + e.getMessage(),
                    "Критична помилка БД", JOptionPane.ERROR_MESSAGE);
        }
    }
}