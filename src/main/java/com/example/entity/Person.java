package com.example.entity;

import lombok.Data;
import java.io.Serializable;

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
    protected Long id;

    /**
     * Повне ім'я (ПІБ) особи.
     */
    protected String fullName;

    /**
     * Контактний номер телефону.
     * Використовується як один з критеріїв пошуку та ідентифікації.
     */
    protected String phone;

    /**
     * Конструктор для ініціалізації базових полів особи.
     * Автоматично генерує унікальний ID.
     *
     * @param fullName        Ім'я особи.
     * @param phone Контактний номер телефону.
     */
    public Person(String fullName, String phone) {
        // Автоматична генерація унікального ID за допомогою UUID
//        this.id = UUID.randomUUID().toString();
        this.fullName = fullName;
        this.phone = phone;
    }
    /**
     * Конструктор для ініціалізації базових полів особи.
     * Автоматично генерує унікальний ID.
     *
     * @param fullName        Ім'я особи.
     * @param phone Контактний номер телефону.
     */
    public Person(Long id, String fullName, String phone) {
        // Автоматична генерація унікального ID за допомогою UUID
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
    }

    /**
     * Повертає строкове представлення особи.
     * Базова реалізація, яка може бути розширена у нащадках.
     *
     * @return рядок у форматі "Ім'я (тел: Номер)".
     */
    @Override
    public String toString() {
        return fullName + " ( тел: " + phone + ")";
    }
}