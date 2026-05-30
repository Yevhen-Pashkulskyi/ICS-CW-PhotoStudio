package com.example.model;

import com.example.entity.Client;
import com.example.entity.Photographer;
import com.example.entity.Order;
import com.example.entity.SessionType;
import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    @Test
    public void testOrderCalculationSimple() {
        // 1. Підготовка даних (Новий клієнт, знижка 0%)
        Client client = new Client("Новий Клієнт", "0931112233", "new@mail.com", false, 0.0);
        Photographer photographer = new Photographer("Петро","0931111111", "Портрети",500.0);
        SessionType session = new SessionType(1L, "Портрет", 2, 1200.0);

        // 2. Створення замовлення
        Order order = new Order();
        order.setClient(client);
        order.setPhotographer(photographer);
        order.setSessionType(session);
        order.setEventDate(Timestamp.valueOf(LocalDateTime.now()));

        // Викликаємо твій метод розрахунку вартості
        double finalCost = order.calculateTotalCost();

        // 3. Перевірка: Очікуємо повну вартість 1200.0, бо знижка 0%
        assertEquals(1200.0, finalCost, 0.01);
    }

    @Test
    public void testOrderCalculationDiscount() {
        // 1. Підготовка (Постійний клієнт, знижка 10%)
        Client regularClient = new Client("Постійний Клієнт", "0935556677", "regular@mail.com", true, 10.0);
        Photographer photographer = new Photographer("Марія","0937777777", "Весілля",600.0);
        SessionType session = new SessionType(2L, "Весілля", 5, 5000.0);

        // 2. Дія
        Order order = new Order();
        order.setClient(regularClient);
        order.setPhotographer(photographer);
        order.setSessionType(session);

        double finalCost = order.calculateTotalCost();

        // 3. Перевірка: 5000 - 10% (500) = 4500.0
        assertEquals(4500.0, finalCost, 0.01);
    }

    @Test
    public void testOrderCalculationNullClient() {
        // 1. Підготовка даних: клієнта немає (null), але тип сесії коштує 1200.0
        Client client = null;
        Photographer photographer = null;
        SessionType session = new SessionType(2L, "Весілля", 1, 1200.0);

        Order order = new Order();
        order.setClient(client);
        order.setPhotographer(photographer);
        order.setSessionType(session);

        // 2. Дія: викликаємо прорахунок
        double finalCost = order.calculateTotalCost();

        // 3. Перевірка: очікуємо повну вартість 1200.0, бо код захищений від null і просто ігнорує знижку
        assertEquals(1200.0, finalCost, 0.01, "Якщо клієнт відсутній (null), повинна повернутися повна вартість послуги");
    }
}