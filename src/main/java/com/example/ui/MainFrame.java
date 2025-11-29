package com.example.ui;

import com.example.control.DataManager;
import com.example.ui.panels.ClientsPanel;
import com.example.ui.panels.DashboardPanel;
import com.example.ui.panels.OrdersPanel;
import com.example.ui.panels.ReportsPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;

public class MainFrame extends JFrame {

    private DataManager dataManager;
    private static final String DATA_DIR_PATH = ".";

    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Зберігаємо посилання на панелі, щоб оновлювати їх
    private OrdersPanel ordersPanel;
    private ClientsPanel clientsPanel;

    public MainFrame() {
        setTitle("Фотоательє");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        dataManager = new DataManager();

        // Меню зліва
        add(createSidebar(), BorderLayout.WEST);

        // Центральна частина
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(new Color(245, 245, 250));

        // Ініціалізація панелей
        ordersPanel = new OrdersPanel(dataManager);
        clientsPanel = new ClientsPanel(dataManager);

        contentPanel.add(new DashboardPanel(this, dataManager), "DASHBOARD");
        contentPanel.add(ordersPanel, "ORDERS");
        contentPanel.add(clientsPanel, "CLIENTS");
        contentPanel.add(new ReportsPanel(dataManager), "REPORTS");

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(50, 60, 80));
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setBorder(new EmptyBorder(20, 10, 20, 10));

        JLabel titleLabel = new JLabel("ФОТОАТЕЛЬЄ");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(titleLabel);
        sidebar.add(Box.createVerticalStrut(40));

        sidebar.add(createMenuButton("Головна", "DASHBOARD"));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createMenuButton("Замовлення", "ORDERS"));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createMenuButton("Клієнти", "CLIENTS"));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createMenuButton("Звіти", "REPORTS"));

        sidebar.add(Box.createVerticalGlue());

        JButton exitBtn = new JButton("Зберегти та Вийти");
        styleButton(exitBtn);
        exitBtn.setBackground(new Color(200, 80, 80));
        exitBtn.addActionListener(e -> {
            try {
                dataManager.saveDataToFile(DATA_DIR_PATH);
                JOptionPane.showMessageDialog(this, "Дані збережено!");
                System.exit(0);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Помилка збереження: " + ex.getMessage());
            }
        });
        sidebar.add(exitBtn);

        return sidebar;
    }

    private JButton createMenuButton(String text, String cardName) {
        JButton btn = new JButton(text);
        styleButton(btn);
        btn.addActionListener(e -> {
            // Перед показом оновлюємо таблиці, щоб бачити свіжі дані
            if (cardName.equals("ORDERS")) ordersPanel.refreshTable();
            if (cardName.equals("CLIENTS")) clientsPanel.refreshTable();
            cardLayout.show(contentPanel, cardName);
        });
        return btn;
    }

    private void styleButton(JButton btn) {
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setBackground(new Color(70, 80, 100));
        btn.setForeground(Color.BLACK);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}