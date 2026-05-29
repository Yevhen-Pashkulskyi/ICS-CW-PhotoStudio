package com.example.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Абстрактний базовий клас для всіх персон у системі (клієнтів, фотографів).
 * Містить спільні атрибути, такі як ідентифікатор, ім'я та контактні дані.
 */
@Data
@NoArgsConstructor
public abstract class Person implements Serializable {

    /**
     * Унікальний ідентифікатор особи в базі даних.
     */
    protected Long id;

    /**
     * Повне ім'я (ПІБ) особи.
     */
    protected String fullName;

    /**
     * Контактний номер телефону.
     */
    protected String phone;

    /**
     * Конструктор для створення нової особи без ID (до збереження в БД).
     */
    public Person(String fullName, String phone) {
        this.fullName = fullName;
        this.phone = phone;
    }

    /**
     * Конструктор для відновлення особи з бази даних (з існуючим ID).
     */
    public Person(Long id, String fullName, String phone) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return fullName + " (тел: " + phone + ")";
    }
}