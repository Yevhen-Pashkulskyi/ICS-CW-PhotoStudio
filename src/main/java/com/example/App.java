package com.example;

import com.example.ui.MainFrame;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        // Запуск GUI у правильному потоці
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}