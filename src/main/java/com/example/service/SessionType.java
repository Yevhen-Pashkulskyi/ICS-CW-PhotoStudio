package com.example.service;

import java.io.Serializable;

/**
 * Клас-довідник, що описує типи доступних фотопослуг (наприклад, "Весільна", "Портретна").
 * <p>
 * Використовується як частина об'єкта {@code Order} (принцип <b>композиції</b>).
 * Слугує джерелом даних для розрахунку базової вартості замовлення.
 */
public class SessionType implements Serializable {

    /**
     * Назва типу фотосесії (наприклад, "Репортажна зйомка").
     * Ця назва відображається у випадаючих списках інтерфейсу.
     */
    private String name;

    /**
     * Базова вартість послуги у гривнях.
     * Це ціна до застосування будь-яких знижок (наприклад, для постійних клієнтів).
     */
    private double basePrice;

    /**
     * Конструктор для створення нового типу послуги.
     * Зазвичай викликається при ініціалізації системи (заповнення довідників).
     *
     * @param name      Назва послуги.
     * @param basePrice Вартість послуги (грн).
     */
    public SessionType(String name, double basePrice) {
        this.name = name;
        this.basePrice = basePrice;
    }

    /**
     * Отримує назву послуги.
     * @return рядок з назвою.
     */
    public String getName() {
        return name;
    }

    /**
     * Отримує базову вартість послуги.
     * Використовується методом {@code calculateTotalCost()} у класі Order.
     * @return ціна (double).
     */
    public double getBasePrice() {
        return basePrice;
    }

    /**
     * Повертає форматований рядок для відображення в компонентах GUI (наприклад, JComboBox).
     * Формат дозволяє користувачеві одразу бачити назву та ціну.
     *
     * @return рядок у форматі "Назва (Ціна грн)".
     */
    @Override
    public String toString() {
        return name + " (" + basePrice + " грн)";
    }
}