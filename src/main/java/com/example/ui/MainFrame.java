package com.example.ui;

import com.example.control.DatabaseManager;
import com.example.control.OrderController;
import com.example.ui.panels.ClientsPanel;
import com.example.ui.panels.DashboardPanel;
import com.example.ui.panels.OrdersPanel;
import com.example.ui.panels.ReportsPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Головне вікно програми (Main Window).
 * Забезпечує навігацію між модулями через CardLayout за схемою Dashboard.
 */
public class MainFrame extends JFrame {

    private final DatabaseManager databaseManager;
    private final OrderController orderController;

    /** Панель-контейнер для відображення змінних екранів (карток). */
    private final JPanel contentPanel;

    /** Менеджер компонування для перемикання екранів. */
    private final CardLayout cardLayout;

    // Зберігаємо прямі посилання на панелі для оперативної синхронізації даних
    private final DashboardPanel dashboardPanel;
    private final OrdersPanel ordersPanel;
    private final ClientsPanel clientsPanel;

    /**
     * Конструктор головного вікна.
     */
    public MainFrame() {
        setTitle("Фотоательє — Система управління");
        setSize(1520, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Ініціалізація менеджерів даних
        databaseManager = new DatabaseManager();
        orderController = new OrderController(databaseManager);

        // Налаштування центрального контейнера карток
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(new Color(245, 245, 250));

        // Ініціалізація панелей
        dashboardPanel = new DashboardPanel(this, orderController);
        ordersPanel = new OrdersPanel(orderController);
        clientsPanel = new ClientsPanel(orderController);

        // Додавання "карток" в CardLayout
        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(ordersPanel, "ORDERS");
        contentPanel.add(clientsPanel, "CLIENTS");
        contentPanel.add(new ReportsPanel(orderController), "REPORTS");

        // Збирання інтерфейсу докупи
        add(createSidebar(), BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Створює бічну панель навігації.
     */
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(40, 50, 65)); // Трохи м'якший темний відтінок
        sidebar.setPreferredSize(new Dimension(220, 0)); // ВИПРАВЛЕНО: Замість getHeight() передаємо 0, BorderLayout сам розтягне по вертикалі
        sidebar.setBorder(new EmptyBorder(25, 15, 25, 15));

        // Логотип / Заголовок системи
        JLabel titleLabel = new JLabel("ФОТОАТЕЛЬЄ");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(titleLabel);
        sidebar.add(Box.createVerticalStrut(40));

        // Навігаційне меню
        sidebar.add(createMenuButton("Головна", "DASHBOARD"));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createMenuButton("Замовлення", "ORDERS"));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createMenuButton("Клієнти", "CLIENTS"));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createMenuButton("Звіти", "REPORTS"));

        // Штовхач кнопки вниз
        sidebar.add(Box.createVerticalGlue());

        // Кнопка безпечного виходу з закриттям ресурсів
        JButton exitBtn = new JButton("Зберегти та Вийти");
        styleButton(exitBtn);
        exitBtn.setBackground(new Color(217, 83, 79)); // Гарний коралово-червоний колір
        exitBtn.setForeground(Color.WHITE);

        exitBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Ви впевнені, що хочете завершити роботу?",
                    "Підтвердження виходу", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        sidebar.add(exitBtn);

        return sidebar;
    }

    /**
     * Створює кнопку меню та забезпечує динамічне оновлення інтерфейсів при перемиканні.
     */
    private JButton createMenuButton(String text, String cardName) {
        JButton btn = new JButton(text);
        styleButton(btn);
        btn.addActionListener(e -> {
            switch (cardName) {
                case "DASHBOARD" -> {
                    if (dashboardPanel != null) {
                        dashboardPanel.refreshStats();
                    }
                }
                case "ORDERS" -> ordersPanel.refreshTable();
                case "CLIENTS" -> clientsPanel.refreshTable();
            }

            // Перемикаємо екран
            cardLayout.show(contentPanel, cardName);
        });
        return btn;
    }

    /**
     * Уніфікована стилізація кнопок управління.
     */
    private void styleButton(JButton btn) {
        btn.setMaximumSize(new Dimension(190, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false); // Виправлено: false повністю прибирає некрасиву внутрішню рамку фокусу Swing
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(33, 37, 41));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}