package com.example.entity;

import java.io.Serializable;

/**
 * Клас, що представляє клієнта фотоательє.
 * Успадковує клас Person.
 */
public class Client extends Person implements Serializable {
    private String email;
    private boolean isRegular; // true - постійний клієнт, false - новий

    public Client(String name, String phoneNumber, String email, boolean isRegular) {
        super(name, phoneNumber); // Виклик конструктора базового класу
        this.email = email;
        this.isRegular = isRegular;
    }

    // Специфічні геттери та сеттери
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isRegular() { return isRegular; }
    public void setRegular(boolean regular) { isRegular = regular; }

    @Override
    public String toString() {
        String status = isRegular ? "Постійний" : "Новий";
        return super.toString() + " [" + status + "]";
    }
}
