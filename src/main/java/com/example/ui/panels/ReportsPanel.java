package com.example.ui.panels;

import com.example.control.DataManager;
import com.example.entity.Photographer;
import com.example.model.Order;
import com.example.util.OrderStatus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class ReportsPanel extends JPanel {

    private DataManager dataManager;
    private JTextArea reportArea;

    public ReportsPanel(DataManager dataManager) {
        this.dataManager = dataManager;
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel title = new JLabel("Аналітика та Звіти");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(title);
        add(topPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(300);

        // Кнопки
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        addButton(buttonPanel, "1. Активні замовлення", this::reportActiveOrders);
        addButton(buttonPanel, "2. Статистика клієнтів", this::reportClients);
        addButton(buttonPanel, "3. Фотографи", this::reportPhotographers);
        addButton(buttonPanel, "4. Список фото (по ID)", this::reportPhotos);
        addButton(buttonPanel, "5. Дохід", this::reportRevenue);
        addButton(buttonPanel, "6. Популярна послуга", this::reportPopularType);

        // Текст
        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        splitPane.setLeftComponent(buttonPanel);
        splitPane.setRightComponent(new JScrollPane(reportArea));
        add(splitPane, BorderLayout.CENTER);
    }

    private void addButton(JPanel panel, String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.addActionListener(e -> action.run());
        panel.add(btn);
    }

    // --- Логіка звітів (скопійована та очищена з MainFrame) ---
    private void reportActiveOrders() {
        StringBuilder sb = new StringBuilder("=== АКТИВНІ ЗАМОВЛЕННЯ ===\n\n");
        sb.append("Кількість: ").append(dataManager.getActiveOrdersCount()).append("\n");
        dataManager.getOrders().stream()
                .filter(o -> o.getStatus() == OrderStatus.NEW || o.getStatus() == OrderStatus.IN_PROGRESS)
                .forEach(o -> sb.append(o.getId().substring(0,8)).append(" - ").append(o.getStatus()).append("\n"));
        reportArea.setText(sb.toString());
    }

    private void reportClients() {
        reportArea.setText("Постійних: " + dataManager.getRegularClientsCount() +
                "\nНових: " + dataManager.getNewClientsCount());
    }

    private void reportPhotographers() {
        StringBuilder sb = new StringBuilder("=== ФОТОГРАФИ ===\n");
        for (Photographer p : dataManager.getPhotographers()) {
            sb.append(p.getName()).append(" (").append(p.getSpecialization()).append(")\n");
        }
        reportArea.setText(sb.toString());
    }

    private void reportPhotos() {
        String id = JOptionPane.showInputDialog(this, "ID замовлення:");
        if (id == null) return;
        Order order = dataManager.getOrders().stream().filter(o -> o.getId().startsWith(id)).findFirst().orElse(null);
        if (order != null) {
            StringBuilder sb = new StringBuilder("Фото для ").append(order.getId()).append("\n");
            dataManager.getPhotosForOrder(order.getId()).forEach(p -> sb.append(p.getFilePath()).append("\n"));
            reportArea.setText(sb.toString());
        } else {
            reportArea.setText("Не знайдено.");
        }
    }

    private void reportRevenue() {
        reportArea.setText("Загальний дохід: " +
                dataManager.getTotalRevenueForPeriod(LocalDateTime.MIN, LocalDateTime.MAX) + " грн");
    }

    private void reportPopularType() {
        reportArea.setText("Популярна: " + dataManager.getMostPopularSessionType().orElse("-"));
    }
}