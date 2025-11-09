package com.example.service;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Клас для управління розкладом (для композиції у Photographer).
 * (Примітка: реалізація логіки (checkAvailability, reserveSlot)
 * буде додана на наступних етапах).
 */
public class Schedule implements Serializable {
    // Приклад структури для зберігання слотів: Дата -> Час -> ID Замовлення
    private Map<LocalDate, Map<LocalTime, String>> bookedSlots;

    public Schedule() {
        this.bookedSlots = new HashMap<>();
    }

    // public boolean checkAvailability(LocalDate date, LocalTime time) { ... }
    // public void reserveSlot(LocalDate date, LocalTime time, String orderId) { ... }
}
