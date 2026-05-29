package com.example.entity;

import com.example.util.PaymentMethod;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Клас, що моделює фінансову транзакцію (оплату).
 * Зберігає інформацію про проведені платежі, зв'язуючи їх з конкретним замовленням.
 */
@Data
@NoArgsConstructor
public class Payment implements Serializable {

    private long id;

    /**
     * Об'єкт замовлення, за яке проводиться оплата (Зв'язок ManyToOne/Foreign Key).
     */
    private Order orderId;
    private double paymentAmount;
    private Timestamp paymentDate;
    private PaymentMethod paymentMethod;

    /**
     * Конструктор для ініціалізації нового платежу в системі.
     */
    public Payment(Order orderId, double paymentAmount, PaymentMethod paymentMethod) {
        this.orderId = orderId;
        this.paymentAmount = paymentAmount;
        this.paymentDate = new Timestamp(System.currentTimeMillis());
        this.paymentMethod = paymentMethod;
    }

    /**
     * Конструктор для завантаження платежу з БД.
     */
    public Payment(long id, Order orderId, double paymentAmount, Timestamp paymentDate, PaymentMethod paymentMethod) {
        this.id = id;
        this.orderId = orderId;
        this.paymentAmount = paymentAmount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String toString() {
        return "Payment [ID=" + id + ", OrderID=" + (orderId != null ? orderId.getId() : "null") + ", Amount=" + paymentAmount + " грн]";
    }
}