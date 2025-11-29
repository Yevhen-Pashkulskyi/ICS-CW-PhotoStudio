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

    // ВАЖЛИВО: Тепер тут типи об'єктів, а не String
    private JComboBox<SessionType> sessionTypeBox;
    private JComboBox<Photographer> photographerBox;

    private JLabel priceLabel;

    public OrderDialog(Frame parent, DataManager dataManager) {
        super(parent, "Створення нового замовлення", true);
        this.dataManager = dataManager;

        setSize(450, 550);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

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

        // ВАЖЛИВО: Створюємо список для об'єктів SessionType
        sessionTypeBox = new JComboBox<>();
        fillSessionTypes(); // Завантажуємо з DataManager

        // При виборі оновлюємо ціну
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
        priceLabel = new JLabel("До сплати: 0.0 грн");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceLabel.setForeground(new Color(0, 100, 0));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(priceLabel);

        add(mainPanel, BorderLayout.CENTER);

        // --- КНОПКИ ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Скасувати");
        cancelBtn.addActionListener(e -> dispose());

        JButton okBtn = new JButton("Підтвердити замовлення");
        okBtn.setBackground(new Color(40, 167, 69));
        okBtn.setForeground(Color.BLACK); // Щоб було видно текст
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

    // ВАЖЛИВО: Заповнюємо реальними даними з DataManager
    private void fillSessionTypes() {
        for (SessionType st : dataManager.getSessionTypes()) {
            sessionTypeBox.addItem(st);
        }
    }

    private void fillPhotographers() {
        List<Photographer> list = dataManager.getPhotographers();
        if (list.isEmpty()) {
            photographerBox.addItem(null);
        } else {
            for (Photographer p : list) {
                photographerBox.addItem(p);
            }
        }
    }

    private void updatePrice() {
        SessionType selected = (SessionType) sessionTypeBox.getSelectedItem();
        if (selected != null) {
            // Беремо ціну прямо з об'єкта, ніякого парсингу тексту!
            priceLabel.setText("До сплати: " + selected.getBasePrice() + " грн");
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
            JOptionPane.showMessageDialog(this, "Оберіть фотографа!", "Помилка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Пошук або створення клієнта (через DataManager!)
        String phone = clientPhoneField.getText().trim();
        Client client = dataManager.findClientByPhone(phone);

        if (client == null) {
            client = new Client(clientNameField.getText(), phone, clientEmailField.getText(), false);
            dataManager.addClient(client);
        }

        // 3. Отримання обраних об'єктів (тепер це просто!)
        SessionType session = (SessionType) sessionTypeBox.getSelectedItem();
        Photographer photographer = (Photographer) photographerBox.getSelectedItem();

        // 4. Створення замовлення
        Order order = new Order(client, photographer, session);
        // Генеруємо випадкову кількість фото від 3 до 10
        int photoCount = 3 + (int)(Math.random() * 8);

        for (int i = 1; i <= photoCount; i++) {
            // Генеруємо "назву файлу"
            String fileName = "IMG_" + (1000 + (int)(Math.random() * 9000)) + ".JPG";
            // Додаємо у замовлення (потрібно імпортувати com.example.entity.Photo)
            order.getPhotos().add(new com.example.entity.Photo(fileName));
        }
        dataManager.addOrder(order);

        succeeded = true;
        JOptionPane.showMessageDialog(this, "Замовлення успішно створено!\nНомер: " + order.getId().substring(0,8));
        dispose();
    }

    public boolean isSucceeded() {
        return succeeded;
    }
}