package com.example.entity;

import com.example.model.Order;
import lombok.Data;

/**
 * Клас, що репрезентує окрему фотографію в межах замовлення.
 * Зберігає інформацію про унікальний ідентифікатор та фізичне розташування файлу.
 * Використовується у списку фотографій об'єкта Order (композиція).
 */
@Data
public class Photo {//implements Serializable {

    private Long id;

    /**
     * Шлях до файлу зображення або його назва у файловій системі.
     */
    private String filePath;
    private Long orderId;

    /**
     * Конструктор для створення нового об'єкта фотографії.
     *
     * @param filePath шлях до файлу або назва файлу.
     * @param orderId    для визначення ід ордера
     */
    public Photo(String filePath, Long orderId) {
        this.filePath = filePath;
        this.orderId = orderId;
    }

    /**
     * Конструктор для зчитування об'єкта фотографії.
     *
     * @param id       ід фото
     * @param filePath шлях до файлу або назва файлу.
     * @param orderId для визначення ід ордера
     */
    public Photo(Long id, String filePath, Long orderId) {
        this.id = id;
        this.filePath = filePath;
        this.orderId = orderId;
    }
}