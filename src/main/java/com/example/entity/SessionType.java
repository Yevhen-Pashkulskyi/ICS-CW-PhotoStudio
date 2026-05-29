package com.example.entity;

import java.io.Serializable;
import lombok.*;

/**
 * Клас-довідник, що описує типи доступних фотопослуг (наприклад, "Весільна", "Портретна").
 * <p>
 * Використовується як частина об'єкта {@code Order} (принцип <b>композиції</b>).
 * Слугує джерелом даних для розрахунку базової вартості замовлення.
 */
@Data
public class SessionType implements Serializable {

   private Long id;
    /**
     * Назва типу фотосесії (наприклад, "Репортажна зйомка").
     * Ця назва відображається у випадаючих списках інтерфейсу.
     */
    private String sessionName;

    // тривалість сесії в годинах
    private int durationHours;

    /**
     * Базова вартість послуги у гривнях.
     * Це ціна до застосування будь-яких знижок (наприклад, для постійних клієнтів).
     */
    private double price;

    /**
     * Конструктор для створення нового типу послуги.
     * Зазвичай викликається при ініціалізації системи (заповнення довідників).
     *
     * @param sessionName      Назва послуги.
     * @param durationHours   тривалість сесії
     * @param price Вартість послуги (грн).
     */
    public SessionType(String sessionName, int durationHours, double price) {
        this.sessionName = sessionName;
        this.durationHours = durationHours;
        this.price = price;
    }
    /**
     * Конструктор для створення нового типу послуги.
     * Зазвичай викликається при ініціалізації системи (заповнення довідників).
     *
     * @param id id сесії
     * @param sessionName      Назва послуги.
     * @param durationHours  тривалість сесії
     * @param price Вартість послуги (грн).
     */
    public SessionType(Long id, String sessionName, int durationHours, double price) {
        this.id = id;
        this.sessionName = sessionName;
        this.durationHours = durationHours;
        this.price = price;
    }

    /**
     * Повертає форматований рядок для відображення в компонентах GUI (наприклад, JComboBox).
     * Формат дозволяє користувачеві одразу бачити назву та ціну.
     *
     * @return рядок у форматі "Назва (Ціна грн)".
     */
    @Override
    public String toString() {
        return sessionName + " (" + price + " грн)";
    }
}