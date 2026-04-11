package com.example.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.UUID;

/**
 * Абстрактний базовий клас для всіх персон у системі (клієнтів, фотографів).
 * Містить спільні атрибути, такі як ідентифікатор, ім'я та контактні дані.
 * Реалізує інтерфейс {@link Serializable} для забезпечення можливості збереження стану об'єктів у файл.
 */
@Data
public abstract class Person implements Serializable {

    /**
     * Унікальний ідентифікатор особи (UUID).
     * Генерується автоматично при створенні нового об'єкта або відновлюється з файлу.
     */
    protected String id;

    /**
     * Повне ім'я (ПІБ) особи.
     */
    protected String name;

    /**
     * Контактний номер телефону.
     * Використовується як один з критеріїв пошуку та ідентифікації.
     */
    protected String phoneNumber;

    /**
     * Конструктор для ініціалізації базових полів особи.
     * Автоматично генерує унікальний ID.
     *
     * @param name        Ім'я особи.
     * @param phoneNumber Контактний номер телефону.
     */
    public Person(String name, String phoneNumber) {
        // Автоматична генерація унікального ID за допомогою UUID
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Повертає строкове представлення особи.
     * Базова реалізація, яка може бути розширена у нащадках.
     *
     * @return рядок у форматі "Ім'я (тел: Номер)".
     */
    @Override
    public String toString() {
        return name + " ( тел: " + phoneNumber + ")";
    }
}