package com.example.ui.panels;

import javax.swing.*;
import java.awt.*;

public class Validate extends Component {

    public boolean validateAll(String name, String phone, String email) {
        if (nameAndPhoneNotEmpty(name, phone)) {
            JOptionPane.showMessageDialog(this, "Ім'я та телефон обов'язкові!",
                    "Помилка", JOptionPane.ERROR_MESSAGE);
            return true;
        }
        if (!phoneValidate(phone)) {
            JOptionPane.showMessageDialog(this, "Не вірний формат телефону (Прикл.: 0501234567)",
                    "Помилка", JOptionPane.ERROR_MESSAGE);
            return true;
        }

        if (!emailValidate(email)){
            JOptionPane.showMessageDialog(this, "Недійсний email", "Помилка",
                    JOptionPane.WARNING_MESSAGE);
            return true;
        }
        return  false;
    }

    public boolean nameAndPhoneNotEmpty(String name, String phone) {
        return name.isEmpty() || phone.isEmpty();
    }

    public boolean phoneValidate(String phone) {
        return phone !=null && phone.matches("\\d+") && phone.length() == 10;
    }

    public boolean emailValidate(String email) {
        return email != null && email.contains("@");
    }

}
