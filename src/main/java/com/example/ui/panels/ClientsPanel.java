package com.example.ui.panels;

import com.example.control.DataManager;
import com.example.entity.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ClientsPanel extends JPanel {

    private DataManager dataManager;
    private DefaultTableModel clientTableModel;

    public ClientsPanel(DataManager dataManager) {
        this.dataManager = dataManager;
        setLayout(new BorderLayout());

        // Верхня панель
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel title = new JLabel("База клієнтів");
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JButton addBtn = new JButton("Додати клієнта");
        addBtn.setBackground(new Color(70, 130, 180));
        addBtn.setForeground(Color.BLACK);
        addBtn.addActionListener(e -> showAddClientDialog());

        topPanel.add(title);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(addBtn);
        add(topPanel, BorderLayout.NORTH);

        // Таблиця
        String[] columns = {"ID", "Ім'я", "Телефон", "Email", "Статус"};
        clientTableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(clientTableModel);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshTable();
    }

    public void refreshTable() {
        clientTableModel.setRowCount(0);
        for (Client c : dataManager.getClients()) {
            Object[] row = {
                    c.getId().substring(0, 8) + "...",
                    c.getName(),
                    c.getPhoneNumber(),
                    c.getEmail(),
                    c.isRegular() ? "Постійний" : "Новий"
            };
            clientTableModel.addRow(row);
        }
    }

    private void showAddClientDialog() {
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();

        Object[] message = {"Ім'я:", nameField, "Телефон:", phoneField, "Email:", emailField};

        int option = JOptionPane.showConfirmDialog(this, message, "Новий клієнт", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            // 1. Перевірка на порожні поля
            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ім'я та телефон обов'язкові!", "Помилка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Перевірка на дублікати
            if (dataManager.clientExists(phone, email)) {
                JOptionPane.showMessageDialog(this,
                        "Клієнт з таким номером телефону або Email вже існує!",
                        "Дублювання даних",
                        JOptionPane.WARNING_MESSAGE);
                return; // Зупиняємо створення
            }

            // 3. Якщо все ок — створюємо
            Client newClient = new Client(name, phone, email, false);
            dataManager.addClient(newClient);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Клієнт успішно доданий!");
        }
    }
}