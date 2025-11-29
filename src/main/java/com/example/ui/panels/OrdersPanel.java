package com.example.ui.panels;

import com.example.control.DataManager;
import com.example.entity.Payment;
import com.example.model.Order;
import com.example.util.OrderStatus;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class OrdersPanel extends JPanel {

    private DataManager dataManager;
    private DefaultTableModel orderTableModel;

    public OrdersPanel(DataManager dataManager) {
        this.dataManager = dataManager;
        setLayout(new BorderLayout());

        // Верхня панель
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel title = new JLabel("Управління замовленнями");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        JButton refreshBtn = new JButton("Оновити список");
        refreshBtn.addActionListener(e -> refreshTable());
        topPanel.add(title);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(refreshBtn);
        add(topPanel, BorderLayout.NORTH);

        // Таблиця
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

        // Нижня панель (Оплата)
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
        for (Order o : dataManager.getOrders()) {
            Object[] row = {
                    o.getId().substring(0, 6),
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

    private void processPayment(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Оберіть замовлення!", "Помилка", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Order selectedOrder = dataManager.getOrders().get(selectedRow);

        if (selectedOrder.getStatus() == OrderStatus.PAID) {
            JOptionPane.showMessageDialog(this, "Вже оплачено!", "Інфо", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Прийняти оплату " + selectedOrder.getTotalCost() + " грн?", "Оплата", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // 1. Ставимо статус ОПЛАЧЕНО
            selectedOrder.setStatus(OrderStatus.PAID);

            // 2. Фіксуємо платіж
            new Payment(selectedOrder.getId(), selectedOrder.getTotalCost());

            // 3. === НОВА ЛОГІКА: Перевіряємо, чи став він постійним ===
            dataManager.checkAndUpgradeClient(selectedOrder.getClient());

            // 4. Оновлюємо таблицю і повідомляємо
            refreshTable();

            if (selectedOrder.getClient().isRegular()) {
                JOptionPane.showMessageDialog(this,
                        "Оплата успішна!\nУВАГА: Цей клієнт досяг 3-х замовлень і отримав статус 'Постійний'!");
            } else {
                JOptionPane.showMessageDialog(this, "Оплата успішна!");
            }
        }
    }
}