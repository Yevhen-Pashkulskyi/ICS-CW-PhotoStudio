package com.example;

import com.example.ui.MainFrame;
import javax.swing.SwingUtilities;

/**
 * Головний клас програми, що містить точку входу (метод {@code main}).
 * Відповідає за ініціалізацію та запуск графічного інтерфейсу користувача.
 */
public class App {

    /**
     * Точка входу в програму.
     * <p>
     * Цей метод запускає графічний інтерфейс у спеціальному потоці
     * "Event Dispatch Thread" (EDT), що є обов'язковою вимогою для коректної
     * та безпечної роботи бібліотеки Swing.
     *
     * @param args аргументи командного рядка (не використовуються).
     */
    public static void main(String[] args) {
        // Використання SwingUtilities.invokeLater гарантує, що створення вікна
        // відбудеться в потоці обробки подій, а не в головному потоці.
        SwingUtilities.invokeLater(() -> {
            // Створення головного вікна
            MainFrame frame = new MainFrame();

            // Відображення вікна на екрані
            frame.setVisible(true);
        });
    }
}