package com.example.util;

/**
 * Перелічування (enum) для фіксації можливих станів замовлення.
 */
public enum OrderStatus {
    NEW, // Нове, щойно створене
    IN_PROGRESS, // В роботі у фотографа
    COMPLETED, // Завершено, очікує оплати
    PAID // Оплачено та видано
}
