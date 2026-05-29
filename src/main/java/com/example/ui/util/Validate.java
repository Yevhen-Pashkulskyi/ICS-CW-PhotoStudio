package com.example.ui.util;

import javax.swing.*;
import java.awt.*;

/**
 * Утилітарний клас для валідації вхідних даних користувача.
 * ВИПРАВЛЕНО: Прибрано важке спадкування від Component, методи переведені в static.
 */
public final class Validate {

    // Приватний конструктор, щоб ніхто не міг створити екземпляр утилітарного класу
    private Validate() {}

    /**
     * Комплексна перевірка даних клієнта.
     * @param parent Компонент-предок (вікно), над яким треба відмалювати помилку.
     * @return true, якщо виявлено помилку; false, якщо дані валідні.
     */
    public static boolean validateAll(Component parent, String name, String phone, String email) {

        // 1. ПЕРШОЧЕРГОВО: Перевірка на обов'язкові поля
        if (name == null || name.isEmpty() || phone == null || phone.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Ім'я та телефон є обов'язковими для заповнення!",
                    "Помилка введення", JOptionPane.ERROR_MESSAGE);
            return true;
        }

        // 2. Перевірка формату імені
        if (!nameValidate(name)) {
            JOptionPane.showMessageDialog(parent, "Невірний формат імені! Довжина має бути від 2 до 50 символів (тільки літери).",
                    "Помилка введення", JOptionPane.ERROR_MESSAGE);
            return true;
        }

        // 3. Перевірка формату телефону
        if (!phoneValidate(phone)) {
            JOptionPane.showMessageDialog(parent, "Невірний формат телефону! Має бути 10 цифр (наприклад: 0501234567).",
                    "Помилка введення", JOptionPane.ERROR_MESSAGE);
            return true;
        }

        // 4. Опціональна перевірка Email (якщо порожній — ігноруємо, якщо введений — валідуємо)
        if (email != null && !email.isEmpty() && !emailValidate(email)) {
            JOptionPane.showMessageDialog(parent, "Введено недійсний формат Email!",
                    "Помилка введення", JOptionPane.WARNING_MESSAGE);
            return true;
        }

        return false;
    }

    public static boolean nameValidate(String name) {
        return name != null && name.length() >= 2 && name.length() <= 50
                && name.matches("^[A-Za-zА-Яа-яҐґЄєІіЇї'\\s]+$");
    }

    public static boolean phoneValidate(String phone) {
        // Перевіряємо, чи це рівно 10 цифр (наприклад, український мобільний без +38)
        return phone != null && phone.matches("^\\d{10}$");
    }

    public static boolean emailValidate(String email) {
        // Базовий регулярний вираз для перевірки структури email
        return email != null && email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    }
}