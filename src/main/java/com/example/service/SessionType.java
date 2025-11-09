package com.example.service;

import java.io.Serializable;

/**
 * Клас-довідник для типів фотосесій (для композиції у Order).
 */
public class SessionType implements Serializable {
    private String name;
    private double basePrice;

    public SessionType(String name, double basePrice) {
        this.name = name;
        this.basePrice = basePrice;
    }

    public String getName() { return name; }
    public double getBasePrice() { return basePrice; }

    @Override
    public String toString() {
        return name + " (" + basePrice + " грн)";
    }
}