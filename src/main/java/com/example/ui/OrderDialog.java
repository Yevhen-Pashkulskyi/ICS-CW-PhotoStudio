package com.example.ui;

import com.example.control.DataManager;
import com.example.entity.Client;
import com.example.entity.Photo;
import com.example.entity.Photographer;
import com.example.model.Order;
import com.example.service.SessionType;
import com.example.ui.panels.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Модальне діалогове вікно для створення нового замовлення (Реалізація Сценарію ВВ1).
 * <p>
 * Надає інтерфейс для:
 * <ul>
 * <li>Введення даних клієнта (ПІБ, телефон, email).</li>
 * <li>Вибору типу фотосесії (з автоматичним розрахунком ціни).</li>
 * <li>Вибору фотографа зі списку доступних.</li>
 * </ul>
 * При підтвердженні створює об'єкт {@link Order}, генерує тестові фотографії
 * та зберігає дані через {@link DataManager}.
 */
public class OrderDialog extends JDialog {

    /**
     * Посилання на центральний контролер даних.
     */
    private DataManager dataManager;

    /**
     * Прапорець успішного завершення операції (true, якщо натиснуто "Підтвердити").
     */
    private boolean succeeded = false;

    // --- Компоненти форми ---

    /**
     * Поле введення імені клієнта.
     */
    private JTextField clientNameField;

    /**
     * Поле введення телефону (ключовий атрибут для пошуку клієнта).
     */
    private JTextField clientPhoneField;

    /**
     * Поле введення електронної пошти.
     */
    private JTextField clientEmailField;

    /**
     * Випадаючий список типів фотосесій (заповнюється об'єктами {@link SessionType}).
     */
    private JComboBox<SessionType> sessionTypeBox;

    /**
     * Випадаючий список фотографів (заповнюється об'єктами {@link Photographer}).
     */
    private JComboBox<Photographer> photographerBox;

    /**
     * Мітка для динамічного відображення розрахованої вартості.
     */
    private JLabel priceLabel;

    /**
     * Конструктор діалогового вікна.
     * Ініціалізує розмітку, створює поля введення та заповнює списки даними.
     *
     * @param parent      Батьківське вікно (для модальності).
     * @param dataManager Екземпляр менеджера даних.
     */
    public OrderDialog(Frame parent, DataManager dataManager) {
        super(parent, "Створення нового замовлення", true); // true = модальне вікно
        this.dataManager = dataManager;

        setSize(450, 550);
        setLocationRelativeTo(parent); // Центрування відносно батьківського вікна
        setLayout(new BorderLayout());

        // Головна панель з вертикальним розташуванням елементів
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Відступи

        // --- БЛОК 1: КЛІЄНТ ---
        mainPanel.add(createHeader("1. Дані Клієнта"));
        mainPanel.add(Box.createVerticalStrut(10));

        clientNameField = addField(mainPanel, "Ім'я:");
        clientPhoneField = addField(mainPanel, "Телефон:");
        clientEmailField = addField(mainPanel, "Email:");

        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(new JSeparator()); // Розділова лінія
        mainPanel.add(Box.createVerticalStrut(20));

        // --- БЛОК 2: ЗАМОВЛЕННЯ ---
        mainPanel.add(createHeader("2. Деталі Замовлення"));
        mainPanel.add(Box.createVerticalStrut(10));

        // Вибір типу сесії
        mainPanel.add(new JLabel("Тип фотосесії:"));
        sessionTypeBox = new JComboBox<>();
        fillSessionTypes(); // Завантаження з DataManager

        // Додавання слухача для оновлення ціни при зміні вибору
        sessionTypeBox.addActionListener(e -> updatePrice());
        mainPanel.add(sessionTypeBox);

        mainPanel.add(Box.createVerticalStrut(10));

        // Вибір фотографа
        mainPanel.add(new JLabel("Фотограф:"));
        photographerBox = new JComboBox<>();
        fillPhotographers(); // Завантаження з DataManager
        mainPanel.add(photographerBox);

        mainPanel.add(Box.createVerticalStrut(20));

        // Відображення ціни
        priceLabel = new JLabel("До сплати: 0.0 грн");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceLabel.setForeground(new Color(0, 100, 0)); // Темно-зелений колір
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(priceLabel);

        add(mainPanel, BorderLayout.CENTER);

        // --- Панель кнопок (OK / Cancel) ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Скасувати");
        cancelBtn.addActionListener(e -> dispose());

        JButton okBtn = new JButton("Підтвердити замовлення");
        okBtn.setBackground(new Color(40, 167, 69)); // Стилізація під "Успіх"
        okBtn.setForeground(Color.BLACK);
        okBtn.setFont(new Font("Arial", Font.BOLD, 12));
        okBtn.addActionListener(e -> onConfirm());

        btnPanel.add(cancelBtn);
        btnPanel.add(okBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // Розрахунок ціни для початкового вибору
        updatePrice();
    }

    // --- Допоміжні методи UI ---

    /**
     * Створює стилізований заголовок секції.
     *
     * @param text Текст заголовка.
     * @return налаштований JLabel.
     */
    private JLabel createHeader(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.BLUE);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    /**
     * Додає пару "Мітка + Текстове поле" на панель.
     *
     * @param panel     Панель-контейнер.
     * @param labelText Текст мітки.
     * @return Посилання на створене текстове поле.
     */
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

    /**
     * Заповнює випадаючий список типів сесій даними з DataManager.
     */
    private void fillSessionTypes() {
        for (SessionType st : dataManager.getSessionTypes()) {
            sessionTypeBox.addItem(st);
        }
    }

    /**
     * Заповнює випадаючий список фотографів даними з DataManager.
     */
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

    /**
     * Оновлює текст мітки ціни на основі обраного типу фотосесії.
     * Викликається автоматично при зміні значення в JComboBox.
     */
    private void updatePrice() {
        SessionType selected = (SessionType) sessionTypeBox.getSelectedItem();
        if (selected != null) {
            priceLabel.setText("До сплати: " + selected.getBasePrice() + " грн");
        }
    }

    // --- ЛОГІКА ОБРОБКИ ПОДІЙ ---

    /**
     * Обробляє натискання кнопки "Підтвердити замовлення".
     * <p>
     * Алгоритм:
     * <ol>
     * <li>Валідація вхідних даних (існування імені, телефону, фотографа).</li>
     * <li>Пошук клієнта в базі або створення нового.</li>
     * <li>Створення об'єкта Order.</li>
     * <li>Генерація тестових фотографій (імітація роботи фотографа).</li>
     * <li>Збереження замовлення через DataManager.</li>
     * </ol>
     */
    private void onConfirm() {
        String name = clientNameField.getText().trim();
        String phone = clientPhoneField.getText().trim();
        String email = clientEmailField.getText().trim();
        // 1. Валідація
        Utils utils = new Utils();
        // 1. Перевірка
        if (utils.validate(name, phone, email)) return;

        if (photographerBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Оберіть фотографа!", "Помилка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Пошук або створення клієнта (через DataManager!)
        Client client = dataManager.findClientByPhone(phone);

        if (client == null) {
            client = new Client(clientNameField.getText(), phone, email, false);
            dataManager.addClient(client);
        }

        // 3. Отримання обраних об'єктів
        SessionType session = (SessionType) sessionTypeBox.getSelectedItem();
        Photographer photographer = (Photographer) photographerBox.getSelectedItem();

        // 4. Створення замовлення
        Order order = new Order(client, photographer, session);

        // Імітація процесу зйомки: генеруємо випадкову кількість фото від 3 до 10
        int photoCount = 3 + (int) (Math.random() * 8);

        for (int i = 1; i <= photoCount; i++) {
            // Генеруємо випадкову назву файлу
            String fileName = "IMG_" + (1000 + (int) (Math.random() * 9000)) + ".JPG";
            // Додаємо об'єкт Photo у список замовлення
            order.getPhotos().add(new Photo(fileName));
        }

        // Збереження в систему
        dataManager.addOrder(order);

        succeeded = true;
        JOptionPane.showMessageDialog(this, "Замовлення успішно створено!\nНомер: " + order.getId().substring(0, 8));
        dispose(); // Закриття вікна
    }

    /**
     * Перевіряє, чи було успішно створено замовлення.
     *
     * @return true, якщо користувач натиснув "Підтвердити" і дані коректні.
     */
    public boolean isSucceeded() {
        return succeeded;
    }
}