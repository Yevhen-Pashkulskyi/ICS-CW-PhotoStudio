package com.example.service;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Клас для управління робочим графіком та перевірки зайнятості.
 * Є частиною сутності Photographer (реалізація принципу <b>композиції</b>).
 * <p>
 * Цей клас відповідає за зберігання інформації про заброньовані часові слоти
 * та дозволяє уникнути накладання замовлень на один і той самий час.
 */
public class Schedule implements Serializable {

    /**
     * Основна структура даних для зберігання розкладу.
     * Використовується вкладена мапа (Nested Map):
     * <ul>
     * <li><b>Зовнішній ключ (LocalDate):</b> Дата зйомки.</li>
     * <li><b>Внутрішній ключ (LocalTime):</b> Час початку зйомки.</li>
     * <li><b>Значення (String):</b> ID замовлення, яке зарезервувало цей слот.</li>
     * </ul>
     */
    private Map<LocalDate, Map<LocalTime, String>> bookedSlots;

    /**
     * Конструктор за замовчуванням.
     * Ініціалізує порожній графік (HashMap) при створенні нового фотографа.
     */
    public Schedule() {
        this.bookedSlots = new HashMap<>();
    }

    // Методи для перевірки доступності та бронювання (закоментовані згідно з планом розробки)
    // public boolean checkAvailability(LocalDate date, LocalTime time) { ... }
    // public void reserveSlot(LocalDate date, LocalTime time, String orderId) { ... }
}