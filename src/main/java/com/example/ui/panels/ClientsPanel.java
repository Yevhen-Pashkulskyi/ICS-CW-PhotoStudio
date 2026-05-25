package com.example.ui.panels;

import com.example.control.DatabaseManager;
import com.example.control.OrderController;
import com.example.entity.Client;
import com.example.ui.util.Validate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Панель графічного інтерфейсу для управління базою клієнтів.
 * Відповідає за відображення списку клієнтів у вигляді таблиці
 * та надає функціонал для додавання нових записів.
 * Є частиною головного вікна програми (вкладка "Клієнти").
 */
public class ClientsPanel extends JPanel {

    /**
     * Посилання на центральний контролер даних.gftr45
     */
    private final OrderController orderController;

    /**
     * Модель даних для таблиці, що дозволяє динамічно оновлювати рядки.
     */
    private final DefaultTableModel clientTableModel;

    /**
     * Конструктор панелі клієнтів.
     * Ініціалізує візуальні компоненти (кнопки, таблицю) та наповнює їх даними.
     *
     * @param orderController екземпляр менеджера даних для доступу до списку клієнтів.
     */
    public ClientsPanel(OrderController orderController) {
        this.orderController = orderController;
        setLayout(new BorderLayout());

        // Верхня панель з заголовком та кнопками
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel title = new JLabel("База клієнтів");
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JButton addBtn = new JButton("Додати клієнта");
        addBtn.setBackground(new Color(70, 130, 180));
        addBtn.setForeground(Color.BLACK);

        // Встановлення обробника події натискання кнопки
        addBtn.addActionListener(e -> showAddClientDialog());

        topPanel.add(title);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(addBtn);
        add(topPanel, BorderLayout.NORTH);

        // Налаштування таблиці
        String[] columns = {"ID", "Ім'я", "Телефон", "Email", "Статус", "Знижка"};
        clientTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Робимо таблицю тільки для читання
            }
        };

        JTable table = new JTable(clientTableModel);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Первинне заповнення таблиці
        refreshTable();
    }

    /**
     * Оновлює вміст таблиці актуальними даними з DataManager.
     * Метод очищує поточні рядки таблиці та заново заповнює її,
     * ітеруючи по списку клієнтів.
     */
    public void refreshTable() {
        clientTableModel.setRowCount(0); // Очищення таблиці
        for (Client c : orderController.getClients()) {
            Object[] row = { // мені здається що масив даних запонен не до кінця але ти мене поправ якщо я не прав?
                    c.getId(),
                    c.getFullName(),
                    c.getPhone(),
                    c.getEmail(),
                    c.isRegular() ? "Постійний" : "Новий", // Текстове представлення статусу
                    c.getDiscountRate() + " %"
            };
            clientTableModel.addRow(row);
        }
    }

    /**
     * Відображає модальне діалогове вікно для додавання нового клієнта.
     * Реалізує логіку валідації введених даних:
     * <ul>
     * <li>Перевірка на порожні поля (ім'я та телефон обов'язкові).</li>
     * <li>Перевірка на дублікати (чи існує вже такий телефон/email).</li>
     * </ul>
     * Якщо валідація успішна, створює нового клієнта та додає його в систему.
     */
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
            Validate validate = new Validate();
            // 1. Перевірка
            if (validate.validateAll(name, phone, email)) return;

            // 2. Перевірка на дублікати (використовує бізнес-логіку DataManager)
            if (orderController.findClient(phone) != null) {
                JOptionPane.showMessageDialog(this,
                        "Клієнт з таким номером телефону або Email вже існує!",
                        "Дублювання даних",
                        JOptionPane.WARNING_MESSAGE);
                return; // Зупиняємо створення, щоб уникнути колізій
            }

            try{
                // 3. Якщо все ок — створюємо об'єкт та зберігаємо
                Client newClient = new Client(name, phone, email, false,0.0);
                orderController.addClient(newClient);
                refreshTable(); // Оновлюємо таблицю, щоб показати нового клієнта
                JOptionPane.showMessageDialog(this, "Клієнт успішно доданий!");
            }catch(Exception e){
                JOptionPane.showMessageDialog(this, "Помилка додавання клієнта: " + e.getMessage(),
                        "Помилка БД", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}