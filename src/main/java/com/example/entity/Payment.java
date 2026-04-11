package com.example.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Клас, що моделює фінансову транзакцію (оплату).
 * Зберігає інформацію про проведені платежі, зв'язуючи їх з конкретним замовленням.
 * Створюється автоматично при виконанні сценарію оплати (ВВ2).
 */
@EqualsAndHashCode()
@Getter
public class Payment implements Serializable {

    /**
     * Унікальний ідентифікатор транзакції (UUID).
     */
    private final String id;

    /**
     * Ідентифікатор замовлення, за яке проводиться оплата.
     * Використовується як зовнішній ключ для зв'язку з об'єктом Order.
     */
    private final String orderId;

    /**
     * Сума оплати у грошовому еквіваленті.
     */
    private final double amount;

    /**
     * Дата та точний час проведення фінансової операції.
     */
    private final LocalDateTime paymentDate;

    /**
     * Конструктор для фіксації нового платежу.
     * Автоматично генерує унікальний ID транзакції та фіксує поточний час.
     *
     * @param orderId ID пов'язаного замовлення, яке оплачується.
     * @param amount  Сума коштів, що була внесена клієнтом.
     */
    public Payment(String orderId, double amount) {
        this.id = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.amount = amount;
        this.paymentDate = LocalDateTime.now(); // Фіксуємо час створення об'єкта як час оплати
    }

    /**
     * Повертає строкове представлення платежу для технічного логування.
     * @return рядок з основною інформацією про транзакцію.
     */
    @Override
    public String toString() {
        return "Payment [ID=" + id + ", OrderID=" + orderId + ", Amount=" + amount + "]";
    }
}