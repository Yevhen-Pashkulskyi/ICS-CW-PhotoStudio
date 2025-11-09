package com.example.service;

import java.io.Serializable;

/**
 * Клас для обліку витратних матеріалів (з діаграми прецедентів).
 * Демонструє використання статичної властивості.
 */
public class InventoryItem implements Serializable {
    private String name;
    private int quantity;

    // Статична властивість (static final), спільна для всіх об'єктів
    public static final String UNIT = "шт.";

    public InventoryItem(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public void addQuantity(int amount) {
        if (amount > 0) this.quantity += amount;
    }

    public String getName() { return name; }
    public int getQuantity() { return quantity; }

    @Override
    public String toString() {
        return name + ": " + quantity + " " + UNIT;
    }
}