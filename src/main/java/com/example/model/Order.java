package com.example.model;

import com.example.entity.Client;
import com.example.entity.Photo;
import com.example.entity.Photographer;
import com.example.util.OrderStatus;
import com.example.service.SessionType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;

/**
 * Центральний клас моделі, що представляє замовлення на фотосесію.
 * Об'єднує всі сутності системи в єдиний процес.
 * <p>
 * Реалізує ключові принципи ООП:
 * <ul>
 * <li><b>Асоціація:</b> посилання на {@link Client} та {@link Photographer} (існують незалежно від замовлення).</li>
 * <li><b>Композиція:</b> {@link SessionType} та список {@link Photo} (є частиною замовлення).</li>
 * </ul>
 */
@Data
public class Order implements Serializable {

    /** Унікальний ідентифікатор замовлення (UUID). */
    private String id;

    /** Дата та час створення замовлення. */
    private LocalDateTime orderDate;

    /** Поточний статус виконання (наприклад, NEW, PAID). */
    private OrderStatus status;

    /** Фінальна вартість замовлення з урахуванням усіх знижок. */
    private double totalCost;

    /** Клієнт, який оформив замовлення. */
    private final Client client;

    /** Фотограф, призначений для виконання замовлення. */
    private final Photographer photographer;

    /** Тип обраної фотосесії (містить назву та базову ціну). */
    private final SessionType sessionType;

    /** Список готових фотографій, прив'язаних до цього замовлення. */
    private final List<Photo> photos;

    /**
     * Конструктор для створення нового замовлення.
     * Ініціалізує зв'язки, генерує ID, встановлює поточний час та розраховує вартість.
     *
     * @param client       Клієнт, що робить замовлення.
     * @param photographer Обраний фотограф.
     * @param sessionType  Тип послуги.
     */
    public Order(Client client, Photographer photographer, SessionType sessionType) {
        this.id = UUID.randomUUID().toString();
        this.client = client;
        this.photographer = photographer;
        this.sessionType = sessionType;
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.NEW; // Початковий статус завжди "Новий"
        this.photos = new ArrayList<>(); // Ініціалізація порожнього списку для майбутніх фото
        this.totalCost = calculateTotalCost(); // Автоматичний розрахунок ціни при створенні
    }

    /**
     * Розраховує фінальну вартість замовлення.
     * Перевіряє статус лояльності клієнта: якщо клієнт є постійним (isRegular),
     * застосовується знижка 10% від базової вартості типу сесії.
     *
     * @return розрахована сума до сплати.
     */
    public double calculateTotalCost() {
        double currentCost = sessionType.getBasePrice();
        if (client.isRegular()) {
            currentCost *= 0.90; // Знижка 10%
        }
        this.totalCost = currentCost;
        return totalCost;
    }

    /**
     * Повертає коротке строкове представлення замовлення для списків UI.
     * @return рядок у форматі "Замовлення [ID] | [Ім'я клієнта] | [Статус]".
     */
    @Override
    public String toString() {
        // substring(0, 8) використовується для скорочення довгого UUID
        return "Замовлення " + id.substring(0, 8) + " | " + client.getName() + " | " + status;
    }
}