package com.example.ui;

import com.example.control.DataManager;
import com.example.entity.Client;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MainFrame extends JFrame {

    private DataManager dataManager;
    // Використовуємо "." для збереження в поточній папці
    private static final String DATA_DIR_PATH = ".";
    private DefaultListModel<Client> clientListModel;

    public MainFrame() {
        setTitle("Фотоательє - Система управління");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        this.dataManager = new DataManager();
        this.clientListModel = new DefaultListModel<>();

        loadData(); // Завантажуємо дані при старті

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Клієнти", createClientsPanel());
        // Можна додати інші вкладки

        add(tabbedPane, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Файл");
        JMenuItem saveItem = new JMenuItem("Зберегти CSV");
        saveItem.addActionListener(e -> saveData());
        fileMenu.add(saveItem);
        setJMenuBar(menuBar);
    }

    private JPanel createClientsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Список клієнтів
        JList<Client> clientList = new JList<>(clientListModel);
        panel.add(new JScrollPane(clientList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addClientButton = new JButton("Додати нового клієнта");

        addClientButton.addActionListener(e -> {
            // Простий діалог для додавання
            String name = JOptionPane.showInputDialog(this, "Введіть ім'я клієнта:");
            if (name != null && !name.trim().isEmpty()) {
                String phone = JOptionPane.showInputDialog(this, "Телефон:");
                String email = JOptionPane.showInputDialog(this, "Email:");

                // Створюємо нового клієнта
                Client newClient = new Client(name, phone, email, false);

                // Додаємо в логіку
                dataManager.addClient(newClient);
                // Додаємо в інтерфейс
                clientListModel.addElement(newClient);
            }
        });

        buttonPanel.add(addClientButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void saveData() {
        try {
            dataManager.saveDataToFile(DATA_DIR_PATH);
            JOptionPane.showMessageDialog(this, "Дані збережено у CSV!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Помилка: " + e.getMessage());
        }
    }

    private void loadData() {
        try {
            dataManager.loadDataFromFile(DATA_DIR_PATH);
            // Оновлюємо UI зі списків DataManager
            clientListModel.clear();
            for (Client c : dataManager.getClients()) {
                clientListModel.addElement(c);
            }
        } catch (IOException e) {
            System.out.println("Дані не знайдено (перший запуск).");
        }
    }
}