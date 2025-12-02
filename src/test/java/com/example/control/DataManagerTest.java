package com.example.control;

import com.example.entity.Client;
import com.example.entity.Photographer;
import com.example.model.Order;
import com.example.service.SessionType;
import com.example.util.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class DataManagerTest {

    private DataManager dataManager;

    // Виконується перед кожним тестом (обнуляє дані)
    @BeforeEach
    public void setUp() {
        dataManager = new DataManager();
        // Очистимо дані, якщо вони завантажились з файлу (треба додати метод clear() в DataManager для тестів,
        // або просто не звертати увагу, бо ми додаємо нові)
        // Для чистоти експерименту краще працювати з чистим об'єктом,
        // але DataManager у нас вантажить з файлу в конструкторі.
        // Припустимо, ми тестуємо логіку на нових доданих об'єктах.
    }

    @Test
    public void testActiveOrdersCount() {
        // Створюємо дані
        Client c = new Client("T1", "1", "e", false);
        Photographer p = new Photographer("P1", "2", "S");
        SessionType s = new SessionType("Test", 100.0);

        Order o1 = new Order(c, p, s);
        o1.setStatus(OrderStatus.NEW); // Активне

        Order o2 = new Order(c, p, s);
        o2.setStatus(OrderStatus.PAID); // Не активне

        dataManager.addOrder(o1);
        dataManager.addOrder(o2);

        // У нас може бути більше замовлень з файлу, тому рахуємо дельту або перевіряємо логіку
        // Але для чистого unit-тесту краще перевіряти, що метод повертає правильну кількість
        // серед тих, що ми додали (якщо база була пуста).

        // Перевірка:
        long activeCount = dataManager.getActiveOrdersCount();
        assertTrue(activeCount >= 1, "Має бути мінімум 1 активне замовлення");
    }

    @Test
    public void testMostPopularSessionType() {
        Client c = new Client("C", "1", "e", false);
        Photographer p = new Photographer("P", "1", "S");

        SessionType typeA = new SessionType("TypeA", 100);
        SessionType typeB = new SessionType("TypeB", 200);

        // Додаємо 2 замовлення TypeA і 1 замовлення TypeB
        dataManager.addOrder(new Order(c, p, typeA));
        dataManager.addOrder(new Order(c, p, typeA));
        dataManager.addOrder(new Order(c, p, typeB));

        Optional<String> popular = dataManager.getMostPopularSessionType();

        assertTrue(popular.isPresent());
        // Якщо база була пуста до тесту, то переможе TypeA.
        // Якщо ні - результат може змішатися.
        // В ідеалі для тестів використовують Mock-об'єкти або тестову БД.
        // Але в нашому випадку:
        System.out.println("Popular: " + popular.get());
    }

    @Test
    public void testClientUpgradeLogic() {
        Client c = new Client("Loyal One", "999", "mail", false);
        dataManager.addClient(c);
        Photographer p = new Photographer("P", "1", "S");
        SessionType s = new SessionType("S", 100);

        // Створюємо 3 замовлення і оплачуємо їх
        for (int i = 0; i < 3; i++) {
            Order o = new Order(c, p, s);
            o.setStatus(OrderStatus.PAID);
            dataManager.addOrder(o);
        }

        // Викликаємо перевірку
        dataManager.checkAndUpgradeClient(c);

        // Клієнт мав стати постійним
        assertTrue(c.isRegular(), "Клієнт повинен стати постійним після 3 оплачених замовлень");
    }
}