package com.example.entity;

import com.example.util.OrderStatus;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Центральний клас моделі, що представляє замовлення на фотосесію.
 * Об'єднує клієнта, фотографа та тип сесії.
 */
@Data
@NoArgsConstructor
public class Order implements Serializable {

    /** Унікальний ідентифікатор замовлення в базі даних. */
    private long id;

    /** Клієнт, який оформив замовлення. */
    private Client client;

    /** Фотограф, призначений для виконання замовлення. */
    private Photographer photographer;

    /** Тип обраної фотосесії. */
    private SessionType sessionType;

    /** Дата та час створення замовлення. */
    private Timestamp createdDate;

    /** Дата та час самої події (зйомки). */
    private Timestamp eventDate;

    /** Дедлайн здачі готових фотографій. */
    private Timestamp deliveryDate;

    /** Поточний статус виконання. */
    private OrderStatus status;

    /** Фінальна вартість замовлення з урахуванням усіх знижок. */
    private double totalCost;

    /**
     * Конструктор для СТВОРЕННЯ НОВОГО замовлення (використовується в UI).
     * Ініціалізує поточний час створення, статус "Новий" та автоматично рахує вартість.
     */
    public Order(Client client, Photographer photographer, SessionType sessionType,
                 Timestamp eventDate, Timestamp deliveryDate) {
        this.client = client;
        this.photographer = photographer;
        this.sessionType = sessionType;
        this.eventDate = eventDate;
        this.deliveryDate = deliveryDate;

        this.createdDate = Timestamp.valueOf(LocalDateTime.now());
        this.status = OrderStatus.NEW;
        this.totalCost = calculateTotalCost();
    }

    /**
     * Конструктор для ВІДНОВЛЕННЯ замовлення з бази даних (використовується в OrderDAO).
     * Приймає всі поля без автоматичної генерації часу чи зміни статусів.
     */
    public Order(long id, Client client, Photographer photographer, SessionType sessionType,
                 Timestamp createdDate, Timestamp eventDate, Timestamp deliveryDate,
                 OrderStatus status, double totalCost) {
        this.id = id;
        this.client = client;
        this.photographer = photographer;
        this.sessionType = sessionType;
        this.createdDate = createdDate;
        this.eventDate = eventDate;
        this.deliveryDate = deliveryDate;
        this.status = status;
        this.totalCost = totalCost;
    }

    /**
     * Розраховує фінальну вартість замовлення на основі персональної знижки клієнта.
     *
     * @return розрахована сума до сплати.
     */
    public double calculateTotalCost() {
        double currentCost = sessionType.getPrice();

        // Використовуємо реальну знижку клієнта з БД (discountRate)
        if (this.client != null && client.isRegular() && client.getDiscountRate() > 0) {
            double discountAmount = currentCost * (client.getDiscountRate() / 100.0);
            currentCost -= discountAmount;
        }

        return currentCost;
    }
}