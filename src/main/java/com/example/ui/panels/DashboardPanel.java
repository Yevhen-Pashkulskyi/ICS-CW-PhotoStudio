package com.example.ui.panels;

import com.example.control.DataManager;
import com.example.ui.OrderDialog;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    private JFrame parentFrame;
    private DataManager dataManager;

    public DashboardPanel(JFrame parentFrame, DataManager dataManager) {
        this.parentFrame = parentFrame;
        this.dataManager = dataManager;
        setLayout(new BorderLayout());
        setOpaque(false);

        // Вітання
        JLabel welcomeLabel = new JLabel("Вітаємо в системі управління!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(60, 60, 60));
        add(welcomeLabel, BorderLayout.NORTH);

        // Центральна частина
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        JButton newOrderBtn = new JButton("+ НОВЕ ЗАМОВЛЕННЯ");
        newOrderBtn.setPreferredSize(new Dimension(300, 80));
        newOrderBtn.setFont(new Font("Arial", Font.BOLD, 20));
        newOrderBtn.setBackground(new Color(40, 167, 69));
        newOrderBtn.setForeground(Color.BLACK);
        newOrderBtn.setFocusPainted(false);

        newOrderBtn.addActionListener(e -> openOrderDialog());

        centerPanel.add(newOrderBtn);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void openOrderDialog() {
        if (dataManager.getPhotographers().isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "Спочатку додайте фотографів!", "Увага", JOptionPane.WARNING_MESSAGE);
            return;
        }
        OrderDialog dialog = new OrderDialog(parentFrame, dataManager);
        dialog.setVisible(true);
    }
}