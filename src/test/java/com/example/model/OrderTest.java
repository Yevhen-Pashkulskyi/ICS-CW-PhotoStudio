package com.example.model;

import com.example.entity.Client;
import com.example.entity.Photographer;
import com.example.service.SessionType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    @Test
    public void testOrderCalculationSimple() {
        // 1. Підготовка даних
        Client client = new Client("Test Client", "000", "mail", false); // Новий клієнт
        Photographer photographer = new Photographer("Photo Man", "111", "General");
        SessionType session = new SessionType("Portrait", 1000.0);

        // 2. Дія
        Order order = new Order(client, photographer, session);

        // 3. Перевірка (Assert)
        // Очікуємо 1000.0, бо клієнт новий (без знижки)
        assertEquals(1000.0, order.getTotalCost(), 0.01);
    }

    @Test
    public void testOrderCalculationDiscount() {
        // 1. Підготовка (Клієнт ПОСТІЙНИЙ - true)
        Client regularClient = new Client("Regular Client", "000", "mail", true);
        Photographer photographer = new Photographer("Photo Man", "111", "General");
        SessionType session = new SessionType("Wedding", 5000.0);

        // 2. Дія
        Order order = new Order(regularClient, photographer, session);

        // 3. Перевірка
        // Очікуємо знижку 10%: 5000 - 500 = 4500
        assertEquals(4500.0, order.getTotalCost(), 0.01);
    }
}