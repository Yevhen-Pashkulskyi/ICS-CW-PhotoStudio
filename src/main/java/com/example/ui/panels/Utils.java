package com.example.ui.panels;

import javax.swing.*;
import java.awt.*;

public class Utils extends Component {
    public boolean validate(String name, String phone, String email) {
        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ім'я та телефон обов'язкові!",
                    "Помилка", JOptionPane.ERROR_MESSAGE);
            return true;
        }
        if (!phone.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Номер телефона має мати лише числа",
                    "Помилка", JOptionPane.WARNING_MESSAGE);
            return true;
        }
        if (phone.length() < 10 || phone.length() > 13) {
            JOptionPane.showMessageDialog(this,
                    "Телефон має бути від 10 до 13 цифр (напр. 099... або 38099...)",
                    "Помилка", JOptionPane.WARNING_MESSAGE);
            return true;
        }
        if (email.isEmpty()) {
            return false;
        } else if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Недійсний email", "Помилка",
                    JOptionPane.WARNING_MESSAGE);
            return true;
        }
        return false;
    }
}
