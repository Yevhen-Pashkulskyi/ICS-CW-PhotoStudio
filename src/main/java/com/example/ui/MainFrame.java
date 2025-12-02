package com.example.ui;

import com.example.control.DataManager;
import com.example.ui.panels.ClientsPanel;
import com.example.ui.panels.DashboardPanel;
import com.example.ui.panels.OrdersPanel;
import com.example.ui.panels.ReportsPanel;
import com.example.util.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;

/**
 * Головне вікно програми (Main Window).
 * <p>
 * Цей клас є контейнером верхнього рівня, що об'єднує всі функціональні модулі системи.
 * Архітектура інтерфейсу побудована за схемою "Dashboard" (Панель керування):
 * <ul>
 * <li><b>Ліва частина (WEST):</b> Статичне бічне меню для навігації.</li>
 * <li><b>Центральна частина (CENTER):</b> Динамічна область, що змінюється залежно від обраного пункту.</li>
 * </ul>
 * Для перемикання між екранами використовується менеджер компонування {@link CardLayout}.
 */
public class MainFrame extends JFrame {

    /** Посилання на центральний контролер даних. */
    private DataManager dataManager;

    /** Шлях до директорії для збереження файлів (поточна папка). */
    private static final String DATA_DIR_PATH = "data";

    /** Панель-контейнер для відображення змінних екранів (карток). */
    private JPanel contentPanel;

    /** Менеджер компонування для перемикання екранів. */
    private CardLayout cardLayout;

    // Зберігаємо прямі посилання на панелі, щоб мати змогу викликати їх методи (наприклад, оновлення таблиць)
    private OrdersPanel ordersPanel;
    private ClientsPanel clientsPanel;

    /**
     * Конструктор головного вікна.
     * Налаштовує розмір, заголовок, ініціалізує контролер даних,
     * створює бічне меню та додає всі функціональні панелі.
     */
    public MainFrame() {
        setTitle("Фотоательє");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Центрування вікна на екрані
        setLayout(new BorderLayout());

        // Ініціалізація логіки (завантаження даних відбувається всередині конструктора DataManager)
        dataManager = new DataManager();

        // Додавання бічного меню (ліва частина)
        add(createSidebar(), BorderLayout.WEST);

        // Налаштування центральної частини
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Відступи від країв
        contentPanel.setBackground(new Color(245, 245, 250)); // Світлий фон робочої області

        // Ініціалізація та додавання панелей
        // Ми створюємо їх тут, щоб передати DataManager
        ordersPanel = new OrdersPanel(dataManager);
        clientsPanel = new ClientsPanel(dataManager);

        // Додавання "карток" в CardLayout з унікальними іменами (ключами)
        contentPanel.add(new DashboardPanel(this, dataManager), "DASHBOARD");
        contentPanel.add(ordersPanel, "ORDERS");
        contentPanel.add(clientsPanel, "CLIENTS");
        contentPanel.add(new ReportsPanel(dataManager), "REPORTS");

        add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Створює бічну панель навігації.
     * Містить логотип, кнопки перемикання розділів та кнопку виходу.
     * @return налаштована панель JPanel.
     */
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(50, 60, 80)); // Темно-синій колір меню
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setBorder(new EmptyBorder(20, 10, 20, 10));

        // Логотип / Заголовок
        JLabel titleLabel = new JLabel("ФОТОАТЕЛЬЄ");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(titleLabel);
        sidebar.add(Box.createVerticalStrut(40));

        // Додавання навігаційних кнопок
        sidebar.add(createMenuButton("Головна", "DASHBOARD"));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createMenuButton("Замовлення", "ORDERS"));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createMenuButton("Клієнти", "CLIENTS"));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createMenuButton("Звіти", "REPORTS"));

        // "Пружина" для притискання кнопки виходу до низу
        sidebar.add(Box.createVerticalGlue());

        // Кнопка безпечного виходу
        JButton exitBtn = new JButton("Зберегти та Вийти");
        styleButton(exitBtn);
        exitBtn.setBackground(new Color(200, 80, 80)); // Червоний відтінок
        exitBtn.addActionListener(e -> {
            try {
                dataManager.saveDataToFile(Constants.DIR);
                JOptionPane.showMessageDialog(this, "Дані збережено!");
                System.exit(0);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Помилка збереження: " + ex.getMessage());
            }
        });
        sidebar.add(exitBtn);

        return sidebar;
    }

    /**
     * Створює кнопку меню та додає логіку перемикання екранів.
     * <p>
     * <b>Важливо:</b> При натисканні на кнопки "Замовлення" або "Клієнти"
     * викликається метод {@code refreshTable()}. Це гарантує, що користувач
     * завжди бачить актуальні дані, навіть якщо вони були змінені в інших вікнах.
     *
     * @param text     Текст на кнопці.
     * @param cardName Ключ (ім'я) картки в CardLayout, яку треба показати.
     * @return налаштована кнопка JButton.
     */
    private JButton createMenuButton(String text, String cardName) {
        JButton btn = new JButton(text);
        styleButton(btn);
        btn.addActionListener(e -> {
            // Оновлення даних перед показом відповідної панелі
            if (cardName.equals("ORDERS")) ordersPanel.refreshTable();
            if (cardName.equals("CLIENTS")) clientsPanel.refreshTable();

            // Перемикання видимого екрану
            cardLayout.show(contentPanel, cardName);
        });
        return btn;
    }

    /**
     * Застосовує єдиний стиль оформлення до кнопок меню.
     * @param btn кнопка для стилізації.
     */
    private void styleButton(JButton btn) {
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(true); // Прибирає рамку фокусу
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setBackground(new Color(70, 80, 100));
        btn.setForeground(Color.BLACK);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}