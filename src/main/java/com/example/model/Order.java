package com.example.model;

import com.example.entity.Client;
import com.example.entity.Photo;
import com.example.entity.Photographer;
import com.example.util.OrderStatus;
import com.example.service.SessionType;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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
@EqualsAndHashCode(callSuper = false)
public class Order implements Serializable {

    /** Унікальний ідентифікатор замовлення (UUID). */
    private long id;

    /** Клієнт, який оформив замовлення. */
    private final Client client;

    /** Фотограф, призначений для виконання замовлення. */
    private final Photographer photographer;

    /** Тип обраної фотосесії (містить назву та базову ціну). */
    private final SessionType sessionType;

    /** Дата та час створення замовлення. */
    private Timestamp createdDate;

    private Timestamp eventDate;

    private Timestamp deliveryDate;


    /** Поточний статус виконання (наприклад, NEW, PAID). */
    private OrderStatus status;

    /** Фінальна вартість замовлення з урахуванням усіх знижок. */
    private double totalCost;




    /** Список готових фотографій, прив'язаних до цього замовлення. */
//    private final List<Photo> photos;

    /**
     * Конструктор для створення нового замовлення.
     * Ініціалізує зв'язки, генерує ID, встановлює поточний час та розраховує вартість.
     *
     * @param client       Клієнт, що робить замовлення.
     * @param photographer Обраний фотограф.
     * @param sessionType  Тип послуги.
     */
    public Order(Client client, Photographer photographer, SessionType sessionType,
                 Timestamp eventDate, Timestamp deliveryDate) {
        this.client = client;
        this.photographer = photographer;
        this.sessionType = sessionType;
        this.createdDate = Timestamp.valueOf(LocalDateTime.now());
        this.eventDate = eventDate;
        this.deliveryDate = deliveryDate;
        this.status = OrderStatus.NEW; // Початковий статус завжди "Новий"
//        this.photos = new ArrayList<>(); // Ініціалізація порожнього списку для майбутніх фото
        this.totalCost = calculateTotalCost(); // Автоматичний розрахунок ціни при створенні
    }

    /**
     * Розраховує фінальну вартість замовлення.
     * Перевіряє статус лояльності клієнта: якщо клієнт є постійним (isRegular),
     * застосовується знижка 10% від базової вартості типу сесії.
     *
     * @return розрахована сума до сплати.
     */
    private double calculateTotalCost() {
        double currentCost = sessionType.getPrice();
        if (client.isRegular()) {
            currentCost *= 0.90; // Знижка 10%
        }
        this.totalCost = currentCost;
        return totalCost;
    }
}