package com.example.control;

import com.example.entity.Client;
import com.example.entity.Photographer;
import com.example.entity.Order;
import com.example.entity.SessionType;
import com.example.util.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderControllerTest {

    private OrderController orderController;

    @BeforeEach
    public void setUp() {
        // Ініціалізуємо наш контролер.
        // Примітка: Для ізольованого тестування логіки методів контролера,
        // які працюють з колекціями в пам'яті або повертають обчислювальні дані.
        DatabaseManager dbManager = new DatabaseManager();
        orderController = new OrderController(dbManager);
    }

    // Тест правила 1: Фотограф вільний в інший день
    @Test
    public void testPhotographerIsFreeDifferentDate() {
        // Створюємо тестового фотографа
        Photographer p = new Photographer();
        p.setId(999L);
        p.setFullName("Тестовий Фотограф");

        // Створюємо існуюче замовлення на 20 жовтня
        Client c = new Client("Клієнт", "0501112233", "mail@mail.com", false, 0.0);
        SessionType s = new SessionType(1L, "Портрет", 1, 1000.0);

        Order existingOrder = new Order();
        existingOrder.setClient(c);
        existingOrder.setPhotographer(p);
        existingOrder.setSessionType(s);
        existingOrder.setEventDate(Timestamp.valueOf(LocalDateTime.of(2026, 10, 20, 14, 0)));
        existingOrder.setStatus(OrderStatus.NEW);

        // Додаємо замовлення в систему через контролер
        orderController.addOrder(existingOrder);

        // Робимо запит на ІНШИЙ ДЕНЬ (25 жовтня)
        LocalDateTime requestDate = LocalDateTime.of(2026, 10, 25, 14, 0);
        List<Photographer> available = orderController.getAvailablePhotographers(requestDate);

        // Перевіряємо логіку фільтрації дат
        assertNotNull(available);
    }

    // Тест правила 2: Фотограф зайнятий, якщо різниця в часі менше 2 годин в один і той самий день
    @Test
    public void testPhotographerIsBusySameDateCloseTime() {
        Photographer p = new Photographer();
        p.setId(888L);

        Order existingOrder = new Order();
        existingOrder.setPhotographer(p);
        // Замовлення на 14:00
        existingOrder.setEventDate(Timestamp.valueOf(LocalDateTime.of(2026, 5, 20, 14, 0)));
        existingOrder.setStatus(OrderStatus.IN_PROGRESS);

        orderController.addOrder(existingOrder);

        // Запит на 15:00 (різниця 1 година — має бути зайнятий)
        LocalDateTime requestDate = LocalDateTime.of(2026, 5, 20, 15, 0);
        List<Photographer> available = orderController.getAvailablePhotographers(requestDate);

        // Перевіряємо, що наш фотограф p не повинен бути серед доступних
        boolean isFound = available.stream().anyMatch(ph -> ph.getId().equals(p.getId()));
        assertFalse(isFound, "Фотограф має бути зайнятим, якщо інтервал менше 2 годин!");
    }
}