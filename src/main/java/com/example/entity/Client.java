package com.example.entity;

import java.io.Serializable;

/**
 * Клас, що представляє клієнта фотоательє.
 * Розширює абстрактний клас {@link Person}, додаючи специфічні атрибути,
 * такі як електронна пошта та статус лояльності.
 */
public class Client extends Person implements Serializable {

    /**
     * Електронна пошта клієнта для зв'язку та відправки фото.
     */
    private String email;

    /**
     * Прапорець статусу клієнта.
     * true - постійний клієнт (має право на знижку), false - новий клієнт.
     */
    private boolean isRegular;

    /**
     * Конструктор для створення нового об'єкта клієнта.
     *
     * @param name        Повне ім'я (ПІБ) клієнта.
     * @param phoneNumber Контактний номер телефону.
     * @param email       Адреса електронної пошти.
     * @param isRegular   Початковий статус (true - постійний, false - новий).
     */
    public Client(String name, String phoneNumber, String email, boolean isRegular) {
        super(name, phoneNumber); // Ініціалізація полів базового класу Person
        this.email = email;
        this.isRegular = isRegular;
    }

    /**
     * Отримує електронну пошту клієнта.
     * @return рядок з email адресою.
     */
    public String getEmail() { return email; }

    /**
     * Встановлює або оновлює електронну пошту клієнта.
     * @param email нова адреса електронної пошти.
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Перевіряє, чи є клієнт постійним.
     * Цей метод використовується в бізнес-логіці для розрахунку вартості замовлення.
     * @return true, якщо клієнт має статус постійного.
     */
    public boolean isRegular() { return isRegular; }

    /**
     * Змінює статус лояльності клієнта.
     * Викликається автоматично, коли клієнт досягає певної кількості замовлень.
     * @param regular true для надання статусу постійного клієнта.
     */
    public void setRegular(boolean regular) { isRegular = regular; }

    /**
     * Повертає строкове представлення клієнта для відображення у списках.
     * Додає текстовий опис статусу до базової інформації про персону.
     * @return рядок у форматі "Ім'я (Телефон) [Статус]".
     */
    @Override
    public String toString() {
        String status = isRegular ? "Постійний" : "Новий";
        return super.toString() + " [" + status + "]";
    }
}
