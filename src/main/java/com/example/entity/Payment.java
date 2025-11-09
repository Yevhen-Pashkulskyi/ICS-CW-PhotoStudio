package com.example.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Клас, що моделює фінансову транзакцію (оплату).
 * Створюється при виконанні сценарію ВВ2.
 */
public class Payment implements Serializable {
    private String id;
    private String orderId; // ID замовлення, яке оплачується
    private double amount; // Сума оплати
    private LocalDateTime paymentDate; // Дата та час оплати

    /**
     * Конструктор для фіксації платежу.
     * @param orderId ID пов'язаного замовлення.
     * @param amount Сума, що була сплачена.
     */
    public Payment(String orderId, double amount) {
        this.id = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.amount = amount;
        this.paymentDate = LocalDateTime.now(); // Фіксуємо час створення
    }

    // --- Геттери ---
    public String getId() { return id; }
    public String getOrderId() { return orderId; }
    public double getAmount() { return amount; }
    public LocalDateTime getPaymentDate() { return paymentDate; }

    @Override
    public String toString() {
        return "Payment [ID=" + id + ", OrderID=" + orderId + ", Amount=" + amount + "]";
    }
}