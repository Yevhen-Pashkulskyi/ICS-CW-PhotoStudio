package com.example.ui.panels;

import com.example.control.DataManager;
import com.example.ui.OrderDialog;

import javax.swing.*;
import java.awt.*;

/**
 * Панель головного екрану (Dashboard) програми.
 * Відображається відразу після запуску та вітає користувача.
 * <p>
 * Основна мета цього класу — надати швидкий та інтуїтивний доступ
 * до найважливішої функції системи: створення нового замовлення (Сценарій ВВ1).
 */
public class DashboardPanel extends JPanel {

    /**
     * Посилання на батьківське вікно (MainFrame).
     * Необхідне для того, щоб діалогові вікна (наприклад, OrderDialog)
     * відкривалися як модальні відносно центру програми.
     */
    private JFrame parentFrame;

    /**
     * Посилання на контролер даних.
     * Передається далі у діалогові вікна для збереження нових замовлень.
     */
    private DataManager dataManager;

    /**
     * Конструктор панелі Dashboard.
     * Налаштовує візуальний стиль, шрифти та розміщує велику кнопку "Нове замовлення"
     * по центру екрану.
     *
     * @param parentFrame посилання на головне вікно програми.
     * @param dataManager екземпляр менеджера даних.
     */
    public DashboardPanel(JFrame parentFrame, DataManager dataManager) {
        this.parentFrame = parentFrame;
        this.dataManager = dataManager;
        setLayout(new BorderLayout());

        // Робимо панель прозорою, щоб було видно фон батьківського контейнера (якщо є)
        setOpaque(false);

        // --- Верхня частина: Вітання ---
        JLabel welcomeLabel = new JLabel("Вітаємо в системі управління!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(60, 60, 60)); // Темно-сірий колір для тексту
        add(welcomeLabel, BorderLayout.NORTH);

        // --- Центральна частина: Кнопка дії ---
        // Використовуємо GridBagLayout для центрування кнопки по вертикалі та горизонталі
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        JButton newOrderBtn = new JButton("+ НОВЕ ЗАМОВЛЕННЯ");
        newOrderBtn.setPreferredSize(new Dimension(300, 80)); // Велика зручна кнопка
        newOrderBtn.setFont(new Font("Arial", Font.BOLD, 20));
        newOrderBtn.setBackground(new Color(40, 167, 69)); // Зелений колір (успіх/дія)
        newOrderBtn.setForeground(Color.BLACK);
        newOrderBtn.setFocusPainted(false); // Прибирає рамку фокусу при натисканні

        // Прив'язка події натискання до методу openOrderDialog
        newOrderBtn.addActionListener(e -> openOrderDialog());

        centerPanel.add(newOrderBtn);
        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Відкриває модальне вікно для створення нового замовлення.
     * <p>
     * Перед відкриттям виконує бізнес-перевірку: чи існують у системі фотографи.
     * Замовлення неможливо створити без виконавця, тому якщо база фотографів порожня,
     * користувачеві буде показано попередження замість діалогу замовлення.
     */
    private void openOrderDialog() {
        // Валідація передумов (Pre-condition check)
        if (dataManager.getPhotographers().isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame,
                    "Спочатку додайте фотографів у систему (вкладка 'Управління' або програмно)!",
                    "Увага",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Створення та відображення діалогу
        OrderDialog dialog = new OrderDialog(parentFrame, dataManager);
        dialog.setVisible(true);
    }
}