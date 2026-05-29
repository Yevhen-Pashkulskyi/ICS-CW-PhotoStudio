package com.example.ui.panels;

import com.example.control.OrderController;
import com.example.ui.OrderDialog;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Панель головного екрану (Dashboard) програми.
 * Відображає інтерактивну бізнес-статистику та кнопку швидкого старту.
 */
public class DashboardPanel extends JPanel {

    private final JFrame parentFrame;
    private final OrderController orderController;

    // Мітки для динамічного оновлення статистики через refreshStats()
    private JLabel clientsStatLabel;
    private JLabel ordersStatLabel;
    private JLabel revenueStatLabel;

    /**
     * Конструктор панелі Dashboard.
     */
    public DashboardPanel(JFrame parentFrame, OrderController orderController) {
        this.parentFrame = parentFrame;
        this.orderController = orderController;

        setLayout(new BorderLayout(0, 30));
        setBorder(new javax.swing.border.EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 250)); // Світлий сучасний фон

        // --- 1. ВЕРХНЯ ЧАСТИНА: Вітання ---
        JLabel welcomeLabel = new JLabel("Система управління фотостудією", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcomeLabel.setForeground(new Color(45, 55, 72));
        add(welcomeLabel, BorderLayout.NORTH);

        // --- 2. ЦЕНТРАЛЬНА ЧАСТИНА: Панелі статистики (KPI Cards) ---
        JPanel statsContainer = new JPanel(new GridLayout(1, 3, 20, 0));
        statsContainer.setOpaque(false);

        // Ініціалізація карток
        clientsStatLabel = new JLabel("0", JLabel.CENTER);
        ordersStatLabel = new JLabel("0", JLabel.CENTER);
        revenueStatLabel = new JLabel("0.00 грн", JLabel.CENTER);

        statsContainer.add(createCard("Клієнтів у базі", clientsStatLabel, new Color(66, 153, 225)));
        statsContainer.add(createCard("Всього замовлень", ordersStatLabel, new Color(72, 187, 120)));
        statsContainer.add(createCard("Загальний виторг", revenueStatLabel, new Color(236, 159, 5)));

        add(statsContainer, BorderLayout.CENTER);

        // --- 3. НИЖНЯ ЧАСТИНА: Велика кнопка швидкої дії ---
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setOpaque(false);

        JButton newOrderBtn = new JButton("+ СТВОРИТИ НОВЕ ЗАМОВЛЕННЯ");
        newOrderBtn.setPreferredSize(new Dimension(350, 70));
        newOrderBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        newOrderBtn.setBackground(new Color(40, 167, 69));
        newOrderBtn.setForeground(Color.WHITE); // Білий текст на зеленому виглядає значно краще
        newOrderBtn.setFocusPainted(false);
        newOrderBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        newOrderBtn.addActionListener(e -> openOrderDialog());

        bottomPanel.add(newOrderBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // Первинний прорахунок метрик при запуску
        refreshStats();
    }

    /**
     * Допоміжний метод для швидкого збирання гарних карток метрик.
     */
    private JPanel createCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(new Color(226, 232, 240), 1, true));

        // Верхня кольорова смужка для акценту
        JPanel topStrip = new JPanel();
        topStrip.setBackground(accentColor);
        topStrip.setPreferredSize(new Dimension(0, 5));
        card.add(topStrip, BorderLayout.NORTH);

        JPanel body = new JPanel(new GridLayout(2, 1));
        body.setOpaque(false);
        body.setBorder(new javax.swing.border.EmptyBorder(15, 10, 15, 10));

        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(113, 128, 150));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(new Color(45, 55, 72));

        body.add(titleLabel);
        body.add(valueLabel);
        card.add(body, BorderLayout.CENTER);

        return card;
    }

    /**
     * ВИПРАВЛЕНО: Метод динамічного оновлення фінансових та кількісних показників.
     * Викликається автоматично головним вікном MainFrame при переході на цю вкладку.
     */
    public void refreshStats() {
        try {
            int clientsCount = orderController.getClientsCount();
            int ordersCount = orderController.getOrdersCount();
            double totalRevenue = orderController.getTotalRevenue();

            clientsStatLabel.setText(String.valueOf(clientsCount));
            ordersStatLabel.setText(String.valueOf(ordersCount));
            revenueStatLabel.setText(String.format("%.2f грн", totalRevenue));
        } catch (Exception e) {
            System.err.println("Помилка оновлення дашборду: " + e.getMessage());
        }
    }

    private void openOrderDialog() {
        if (orderController.getPhotographers().isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame,
                    "Спочатку додайте фотографів у систему за допомогою консолі або міграцій БД!",
                    "Увага",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        OrderDialog dialog = new OrderDialog(parentFrame, orderController);
        dialog.setVisible(true);

        // Якщо замовлення успішно створено, відразу оновлюємо цифри на екрані
        if (dialog.isSucceeded()) {
            refreshStats();
        }
    }
}