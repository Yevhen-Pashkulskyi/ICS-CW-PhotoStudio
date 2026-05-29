package com.example.ui;

import com.example.control.OrderController;
import com.example.entity.Client;
import com.example.entity.Photographer;
import com.example.entity.Order;
import com.example.entity.SessionType;
import com.example.ui.util.Validate;
import lombok.Getter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Модальне діалогове вікно для створення нового замовлення
 */
public class OrderDialog extends JDialog {

    /** Посилання на центральний контролер даних. */
    private final OrderController orderController;

//    Прапорець успішного завершення операції.

    @Getter
    private boolean succeeded = false;

    // --- Компоненти форми ---
    private final JTextField clientNameField;
    private final JTextField clientPhoneField;
    private final JTextField clientEmailField;
    private final JComboBox<SessionType> sessionTypeBox;
    private final JComboBox<Photographer> photographerBox;
    private final JLabel priceLabel;
    private final JSpinner eventDateSpinner;
    private final JSpinner deliveryDateSpinner;

    /**
     * Конструктор діалогового вікна.
     */
    public OrderDialog(Frame parent, OrderController orderController) {
        super(parent, "Створення нового замовлення", true);
        this.orderController = orderController;

        setSize(460, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Головна панель з вертикальним розташуванням елементів
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- БЛОК 1: КЛІЄНТ ---
        mainPanel.add(createHeader("1. Дані Клієнта"));
        mainPanel.add(Box.createVerticalStrut(10));

        clientNameField = addField(mainPanel, "Ім'я:");
        clientPhoneField = addField(mainPanel, "Телефон:");
        clientEmailField = addField(mainPanel, "Email:");

        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(new JSeparator());
        mainPanel.add(Box.createVerticalStrut(15));

        // --- БЛОК 2: ЗАМОВЛЕННЯ ---
        mainPanel.add(createHeader("2. Деталі Замовлення"));
        mainPanel.add(Box.createVerticalStrut(10));

        // Вибір типу сесії
        mainPanel.add(new JLabel("Тип фотосесії:"));
        sessionTypeBox = new JComboBox<>();
        fillSessionTypes();
        sessionTypeBox.addActionListener(e -> updatePrice());
        mainPanel.add(sessionTypeBox);

        mainPanel.add(Box.createVerticalStrut(10));

        // Вибір фотографа
        mainPanel.add(new JLabel("Фотограф:"));
        photographerBox = new JComboBox<>();
        fillPhotographers();
        mainPanel.add(photographerBox);

        mainPanel.add(Box.createVerticalStrut(10));

        // Налаштування полів дат через Spinner
        mainPanel.add(new JLabel("Дата та час події:"));
        SpinnerDateModel eventModel = new SpinnerDateModel();
        eventDateSpinner = new JSpinner(eventModel);
        eventDateSpinner.setEditor(new JSpinner.DateEditor(eventDateSpinner, "yyyy-MM-dd HH:mm"));
        mainPanel.add(eventDateSpinner);

        mainPanel.add(Box.createVerticalStrut(10));

        mainPanel.add(new JLabel("Дата здачі матеріалу:"));
        SpinnerDateModel deliveryModel = new SpinnerDateModel();
        deliveryDateSpinner = new JSpinner(deliveryModel);
        deliveryDateSpinner.setEditor(new JSpinner.DateEditor(deliveryDateSpinner, "yyyy-MM-dd HH:mm"));
        mainPanel.add(deliveryDateSpinner);

        mainPanel.add(Box.createVerticalStrut(20));

        // Відображення базової ціни
        priceLabel = new JLabel("До сплати: 0.00 грн");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceLabel.setForeground(new Color(0, 100, 0));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(priceLabel);

        add(mainPanel, BorderLayout.CENTER);

        // --- Панель кнопок (OK / Cancel) ---
        JPanel btnPanel = createButtonPanel();
        add(btnPanel, BorderLayout.SOUTH);

        // Початковий розрахунок ціни
        updatePrice();
    }

    private JPanel createButtonPanel() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Скасувати");
        cancelBtn.addActionListener(e -> dispose());

        JButton okBtn = new JButton("Підтвердити замовлення");
        okBtn.setBackground(new Color(40, 167, 69));
        okBtn.setForeground(Color.BLACK);
        okBtn.setFont(new Font("Arial", Font.BOLD, 12));
        okBtn.addActionListener(e -> onConfirm());

        btnPanel.add(cancelBtn);
        btnPanel.add(okBtn);
        return btnPanel;
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

    private void fillSessionTypes() {
        for (SessionType st : orderController.getSessionTypes()) {
            sessionTypeBox.addItem(st);
        }
    }

    private void fillPhotographers() {
        List<Photographer> list = orderController.getPhotographers();
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
            priceLabel.setText(String.format("До сплати (базова): %.2f грн", selected.getPrice()));
        }
    }

    // --- ЛОГІКА ОБРОБКИ ПОДІЙ ---

    private void onConfirm() {
        String name = clientNameField.getText().trim();
        String phone = clientPhoneField.getText().trim();
        String email = clientEmailField.getText().trim();

        // 1. Валідація текстових полів
        if (Validate.validateAll(this, name, phone, email)) return;

        if (photographerBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Оберіть фотографа!", "Помилка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Photographer selectedPhotographer = (Photographer) photographerBox.getSelectedItem();
        SessionType session = (SessionType) sessionTypeBox.getSelectedItem();

        // Зчитування дат зі Spinner
        java.util.Date eventUtilDate = (java.util.Date) eventDateSpinner.getValue();
        java.util.Date deliveryUtilDate = (java.util.Date) deliveryDateSpinner.getValue();

        Timestamp eventDate = new Timestamp(eventUtilDate.getTime());
        Timestamp deliveryDate = new Timestamp(deliveryUtilDate.getTime());
        LocalDateTime eventDateTime = eventDate.toLocalDateTime();

        // Додаткова перевірка: чи не в минулому часі призначено зйомку
        if (eventDateTime.isBefore(LocalDateTime.now())) {
            JOptionPane.showMessageDialog(this, "Дата події не може бути в минулому!", "Помилка дати", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (deliveryDate.before(eventDate)) {
            JOptionPane.showMessageDialog(this, "Дата здачі матеріалу не може бути раніше за саму зйомку!", "Помилка дати", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Перевірка зайнятості фотографа
        List<Photographer> freePhotographers = orderController.getAvailablePhotographers(eventDateTime);
        boolean isBusy = freePhotographers.stream()
                .noneMatch(p -> p.getId().equals(selectedPhotographer.getId()));

        if (isBusy) {
            JOptionPane.showMessageDialog(this,
                    "Увага! Фотограф " + selectedPhotographer.getFullName() +
                            " вже зайнятий на цей час (" + eventDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + ").\n" +
                            "Оберіть іншого фахівця або змініть дату події.",
                    "Фотограф зайнятий",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Пошук або створення клієнта
        Client client = orderController.findClient(phone);
        if (client == null) {
            client = new Client(name, phone, email, false, 0.0);
            orderController.addClient(client);
        }

        // 3. Створення замовлення та РОЗРАХУНОК ЗНИЖКИ (Виправлено бізнес-логіку)
        Order order = new Order(client, selectedPhotographer, session, eventDate, deliveryDate);

        double basePrice = session.getPrice();
        double discountRate = client.getDiscountRate(); // Наприклад, 10.0 (%)
        double finalCost = basePrice * (1.0 - (discountRate / 100.0));
        order.setTotalCost(finalCost);

        order.setCreatedDate(new Timestamp(System.currentTimeMillis()));

        try {
            // Зберігаємо замовлення
            orderController.addOrder(order);
            succeeded = true;

            // Гарне інформаційне повідомлення для користувача
            String successMsg = "Замовлення успішно створено!\nНомер: " + order.getId() +
                    String.format("\nФінальна вартість: %.2f грн", finalCost);
            if (discountRate > 0) {
                successMsg += String.format(" (Враховано знижку клієнта %.0f%%)", discountRate);
            }

            JOptionPane.showMessageDialog(this, successMsg, "Успіх", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Помилка при збереженні замовлення: " + e.getMessage(),
                    "Помилка БД", JOptionPane.ERROR_MESSAGE);
        }
    }

}