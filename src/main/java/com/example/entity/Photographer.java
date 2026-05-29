package com.example.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Клас, що представляє співробітника-фотографа у системі.
 * Успадковує базові властивості людини від класу {@link Person}.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class Photographer extends Person implements Serializable {

    /**
     * Основна спеціалізація фотографа (наприклад, "Весільна", "Портретна", "Репортаж").
     * Використовується для фільтрації списку при створенні замовлення.
     */
    private String specialization;
    private double baseRate;



    /**
     * Конструктор для створення нового фотографа.
     * Ініціалізує персональні дані
     *
     * @param name           ПІБ фотографа.
     * @param phoneNumber    Контактний номер телефону.
     * @param specialization Напрямок діяльності (спеціалізація).
     */
    public Photographer(String name, String phoneNumber, String specialization, double baseRate) {
        super(name, phoneNumber); // Виклик конструктора базового класу Person
        this.specialization = specialization;
        this.baseRate = baseRate;
        // Об'єкт розкладу створюється разом з фотографом (жорстка композиція)
    }
    /**
     * Конструктор для запису нового фотографа.
     * Ініціалізує персональні дані
     *
     * @param name           ПІБ фотографа.
     * @param phoneNumber    Контактний номер телефону.
     * @param specialization Напрямок діяльності (спеціалізація).
     */
    public Photographer(Long id, String name, String phoneNumber, String specialization, double baseRate) {
        super(id, name, phoneNumber); // Виклик конструктора базового класу Person
        this.specialization = specialization;
        this.baseRate = baseRate;
    }

    /**
     * Повертає строкове представлення фотографа для відображення у списках GUI.
     * @return рядок у форматі "Ім'я [Спеціалізація]".
     */
    @Override
    public String toString() {
        // Використовуємо метод getName() замість this, щоб уникнути рекурсії
        return getFullName() + " [" + specialization + "]";
    }
}