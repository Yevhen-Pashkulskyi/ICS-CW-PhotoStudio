package com.example.entity;

import java.io.Serializable;
import java.util.UUID;

/**
 * Абстрактний базовий клас для всіх персон у системі (клієнтів, фотографів).
 * Реалізує Serializable для можливості збереження у файл.
 */
public abstract class Person implements Serializable {

    protected String id;
    protected String name;
    protected String phoneNumber;

    public Person(String name, String phoneNumber) {
        // Автоматична генерація унікального ID
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return name + " ( тел: " + phoneNumber + ")";
    }
}
