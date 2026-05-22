package com.example.entity;

import com.example.model.Order;
import com.example.util.PaymentMethod;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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
    private final long id;

    /**
     * Ідентифікатор замовлення, за яке проводиться оплата.
     * Використовується як зовнішній ключ для зв'язку з об'єктом Order.
     */
    private final Order orderId;

    /**
     * Сума оплати у грошовому еквіваленті.
     */
    private final double paymentAmount;

    /**
     * Дата та точний час проведення фінансової операції.
     */
    private final Timestamp paymentDate;

    private final PaymentMethod paymentMethod;

    /**
     * Конструктор для фіксації нового платежу.
     * Автоматично генерує унікальний ID транзакції та фіксує поточний час.
     *
     * @param orderId ID пов'язаного замовлення, яке оплачується.
     * @param paymentAmount  Сума коштів, що була внесена клієнтом.
     */
    public Payment(long id, Order orderId, double paymentAmount) {
        this.id = id;
//        this.id = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.paymentAmount = paymentAmount;
        this.paymentDate = Timestamp.valueOf(LocalDateTime.now()); // Фіксуємо час створення об'єкта як час оплати
        this.paymentMethod = PaymentMethod.CARD;
    }

    /**
     * Повертає строкове представлення платежу для технічного логування.
     * @return рядок з основною інформацією про транзакцію.
     */
    @Override
    public String toString() {
        return "Payment [ID=" + id + ", OrderID=" + orderId + ", Amount=" + paymentAmount + "]";
    }
}