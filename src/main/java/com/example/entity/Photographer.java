package com.example.entity;

import com.example.service.Schedule;
import java.io.Serializable;

/**
 * Клас, що представляє співробітника-фотографа у системі.
 * Успадковує базові властивості людини від класу {@link Person}.
 * Реалізує принцип композиції, включаючи об'єкт {@link Schedule} як невід'ємну частину.
 */
public class Photographer extends Person implements Serializable {

    /**
     * Основна спеціалізація фотографа (наприклад, "Весільна", "Портретна", "Репортаж").
     * Використовується для фільтрації списку при створенні замовлення.
     */
    private String specialization;

    /**
     * Особистий графік роботи фотографа.
     * Реалізує композицію: графік створюється разом з фотографом і не існує окремо.
     */
    private Schedule schedule;

    /**
     * Конструктор для створення нового фотографа.
     * Ініціалізує персональні дані та створює новий порожній розклад.
     *
     * @param name           ПІБ фотографа.
     * @param phoneNumber    Контактний номер телефону.
     * @param specialization Напрямок діяльності (спеціалізація).
     */
    public Photographer(String name, String phoneNumber, String specialization) {
        super(name, phoneNumber); // Виклик конструктора базового класу Person
        this.specialization = specialization;
        // Об'єкт розкладу створюється разом з фотографом (жорстка композиція)
        this.schedule = new Schedule();
    }

    /**
     * Отримує спеціалізацію фотографа.
     * @return рядок з назвою спеціалізації.
     */
    public String getSpecialization() { return specialization; }

    /**
     * Повертає об'єкт розкладу фотографа.
     * Використовується системою для перевірки вільних слотів (getAvailablePhotographers).
     * @return об'єкт Schedule.
     */
    public Schedule getSchedule() { return schedule; }

    /**
     * Повертає строкове представлення фотографа для відображення у списках GUI.
     * @return рядок у форматі "Ім'я [Спеціалізація]".
     */
    @Override
    public String toString() {
        // Використовуємо метод getName() замість this, щоб уникнути рекурсії
        return getName() + " [" + specialization + "]";
    }
}