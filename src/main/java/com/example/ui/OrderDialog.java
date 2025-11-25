package com.example.ui;

import com.example.control.DataManager;
import com.example.entity.Client;
import com.example.entity.Photographer;
import com.example.model.Order;
import com.example.service.SessionType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class OrderDialog extends JDialog {

    private DataManager dataManager;
    private boolean succeeded = false;

    // Поля форми
    private JTextField clientNameField;
    private JTextField clientPhoneField;
    private JTextField clientEmailField;

    private JComboBox<String> sessionTypeBox;
    private JComboBox<Photographer> photographerBox;
    private JLabel priceLabel;

    public OrderDialog(Frame parent, DataManager dataManager) {
        super(parent, "Створення нового замовлення", true);
        this.dataManager = dataManager;

        setSize(450, 550);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Основна панель з відступами
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- БЛОК 1: КЛІЄНТ ---
        mainPanel.add(createHeader("1. Дані Клієнта"));
        mainPanel.add(Box.createVerticalStrut(10));

        clientNameField = addField(mainPanel, "ПІБ Клієнта:");
        clientPhoneField = addField(mainPanel, "Телефон:");
        clientEmailField = addField(mainPanel, "Email:");

        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(new JSeparator());
        mainPanel.add(Box.createVerticalStrut(20));

        // --- БЛОК 2: ЗАМОВЛЕННЯ ---
        mainPanel.add(createHeader("2. Деталі Замовлення"));
        mainPanel.add(Box.createVerticalStrut(10));

        // Тип сесії
        mainPanel.add(new JLabel("Тип фотосесії:"));
        String[] types = {
                "Портретна (1000 грн)",
                "Сімейна (2000 грн)",
                "Весільна (5000 грн)",
                "Репортаж (1500 грн)"
        };
        sessionTypeBox = new JComboBox<>(types);
        sessionTypeBox.addActionListener(e -> updatePrice());
        mainPanel.add(sessionTypeBox);

        mainPanel.add(Box.createVerticalStrut(10));

        // Фотограф
        mainPanel.add(new JLabel("Фотограф:"));
        photographerBox = new JComboBox<>();
        fillPhotographers();
        mainPanel.add(photographerBox);

        mainPanel.add(Box.createVerticalStrut(20));

        // Ціна
        priceLabel = new JLabel("До сплати: 1000.0 грн");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceLabel.setForeground(new Color(0, 100, 0)); // Темно-зелений
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(priceLabel);

        add(mainPanel, BorderLayout.CENTER);

        // --- КНОПКИ ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Скасувати");
        cancelBtn.setForeground(Color.BLACK);
        cancelBtn.addActionListener(e -> dispose());

        JButton okBtn = new JButton("Підтвердити замовлення");
        okBtn.setBackground(new Color(40, 167, 69));
        okBtn.setForeground(Color.BLACK); // Чорний текст
        okBtn.setFont(new Font("Arial", Font.BOLD, 12));
        okBtn.addActionListener(e -> onConfirm());

        btnPanel.add(cancelBtn);
        btnPanel.add(okBtn);
        add(btnPanel, BorderLayout.SOUTH);

        updatePrice(); // Початковий розрахунок
    }

    // --- Допоміжні методи UI ---

    private JLabel createHeader(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.BLUE);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField addField(JPanel panel, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);

        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(field);
        panel.add(Box.createVerticalStrut(5));
        return field;
    }

    private void fillPhotographers() {
        List<Photographer> list = dataManager.getPhotographers();
        if (list.isEmpty()) {
            photographerBox.addItem(null); // Щоб не було помилок при рендері
        } else {
            for (Photographer p : list) {
                photographerBox.addItem(p);
            }
        }
    }

    private void updatePrice() {
        String selected = (String) sessionTypeBox.getSelectedItem();
        if (selected != null) {
            // Витягуємо число з дужок "Портретна (1000 грн)"
            String priceStr = selected.replaceAll("[^0-9]", "");
            priceLabel.setText("До сплати: " + priceStr + " грн");
        }
    }

    // --- ЛОГІКА ---

    private void onConfirm() {
        // 1. Валідація
        if (clientNameField.getText().trim().isEmpty() || clientPhoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введіть ім'я та телефон клієнта!", "Помилка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (photographerBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Оберіть фотографа! (Спочатку додайте їх у систему)", "Помилка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Пошук або створення клієнта
        String phone = clientPhoneField.getText().trim();
        Client client = findClientByPhone(phone);

        if (client == null) {
            // Створюємо нового
            client = new Client(clientNameField.getText(), phone, clientEmailField.getText(), false);
            dataManager.addClient(client);
        } else {
            // Оновлюємо дані існуючого, якщо треба? Ні, просто використовуємо.
            // Можна показати повідомлення: "Знайдено постійного клієнта!"
        }

        // 3. Створення SessionType
        String selectedTypeStr = (String) sessionTypeBox.getSelectedItem();
        String typeName = selectedTypeStr.split("\\(")[0].trim();
        double price = Double.parseDouble(selectedTypeStr.replaceAll("[^0-9]", ""));
        SessionType session = new SessionType(typeName, price);

        // 4. Створення замовлення
        Photographer photographer = (Photographer) photographerBox.getSelectedItem();
        Order order = new Order(client, photographer, session);

        dataManager.addOrder(order);

        succeeded = true;

        JOptionPane.showMessageDialog(this, "Замовлення успішно створено!\nНомер: " + order.getId().substring(0,8));
        dispose();
    }

    // Простий пошук клієнта (можна винести в DataManager)
    private Client findClientByPhone(String phone) {
        for (Client c : dataManager.getClients()) {
            if (c.getPhoneNumber().equals(phone)) {
                return c;
            }
        }
        return null;
    }

    public boolean isSucceeded() {
        return succeeded;
    }
}