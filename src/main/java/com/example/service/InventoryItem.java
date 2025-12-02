package com.example.service;

import java.io.Serializable;

/**
 * Клас для обліку витратних матеріалів на складі фотоательє (наприклад, фотопапір, рамки).
 * <p>
 * Цей клас демонструє виконання вимоги курсової роботи щодо використання
 * <b>статичних даних</b> (keyword {@code static}).
 */
public class InventoryItem implements Serializable {

    /**
     * Назва матеріалу або товару.
     */
    private String name;

    /**
     * Поточна кількість одиниць на складі.
     */
    private int quantity;

    /**
     * Статична константа, що визначає одиницю виміру для всіх товарів цього типу.
     * Оскільки поле оголошено як {@code static}, воно є спільним для всіх екземплярів класу
     * і не дублюється в пам'яті для кожного об'єкта.
     */
    public static final String UNIT = "шт.";

    /**
     * Конструктор для створення нової позиції на складі.
     *
     * @param name     Назва матеріалу.
     * @param quantity Початкова кількість.
     */
    public InventoryItem(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    /**
     * Збільшує кількість матеріалу на складі (надходження товару).
     * Містить перевірку вхідних даних: кількість не може бути від'ємною.
     *
     * @param amount Кількість, яку потрібно додати.
     */
    public void addQuantity(int amount) {
        if (amount > 0) {
            this.quantity += amount;
        }
    }

    /**
     * Отримує назву матеріалу.
     * @return рядок з назвою.
     */
    public String getName() {
        return name;
    }

    /**
     * Отримує поточний залишок на складі.
     * @return ціле число (кількість).
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Повертає строкове представлення товару.
     * Використовує статичне поле {@link #UNIT} для форматування виводу.
     *
     * @return рядок у форматі "Назва: Кількість шт.".
     */
    @Override
    public String toString() {
        return name + ": " + quantity + " " + UNIT;
    }
}