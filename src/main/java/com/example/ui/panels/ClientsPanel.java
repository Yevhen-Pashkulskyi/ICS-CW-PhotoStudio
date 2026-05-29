package com.example.ui.panels;

import com.example.control.OrderController;
import com.example.entity.Client;
import com.example.ui.util.Validate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Панель графічного інтерфейсу для управління базою клієнтів.
 */
public class ClientsPanel extends JPanel {

    /** Посилання на центральний контролер даних. */
    private final OrderController orderController;

    /** Модель даних для таблиці, що дозволяє динамічно оновлювати рядки. */
    private final DefaultTableModel clientTableModel;

    /**
     * Конструктор панелі клієнтів.
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
                return false; // Таблиця тільки для читання
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
     * Оновлює вміст таблиці актуальними даними з контролера.
     */
    public void refreshTable() {
        clientTableModel.setRowCount(0); // Очищення таблиці
        for (Client c : orderController.getClients()) {

            Object[] row = {
                    c.getId(),
                    c.getFullName(),
                    c.getPhone(),
                    c.getEmail(),
                    c.isRegular() ? "Постійний" : "Новий",
                    c.getDiscountRate() + " %"
            };
            clientTableModel.addRow(row);
        }
    }

    /**
     * Відображає модальне діалогове вікно для додавання нового клієнта.
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

            if (Validate.validateAll(this, name, phone, email)) return;

            // 2. Перевірка на дублікати за номером телефону
            // ВИПРАВЛЕНО: Текст попередження тепер чітко відповідає логіці (перевірка за телефоном)
            if (orderController.findClient(phone) != null) {
                JOptionPane.showMessageDialog(this,
                        "Клієнт з таким номером телефону вже існує!",
                        "Дублювання даних",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // 3. Якщо все ок — створюємо об'єкт із початковою нульовою знижкою
                Client newClient = new Client(name, phone, email, false, 0.0);
                orderController.addClient(newClient);
                refreshTable(); // Синхронно оновлюємо UI
                JOptionPane.showMessageDialog(this, "Клієнт успішно доданий!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Помилка додавання клієнта: " + e.getMessage(),
                        "Помилка БД", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}