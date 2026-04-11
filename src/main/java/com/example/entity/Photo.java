package com.example.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.UUID;

/**
 * Клас, що репрезентує окрему фотографію в межах замовлення.
 * Зберігає інформацію про унікальний ідентифікатор та фізичне розташування файлу.
 * Використовується у списку фотографій об'єкта Order (композиція).
 */
@Data
public class Photo implements Serializable {

    /**
     * Унікальний ідентифікатор фотографії.
     */
    private String id;

    /**
     * Шлях до файлу зображення або його назва у файловій системі.
     */
    private String filePath;

    /**
     * Конструктор для створення нового об'єкта фотографії.
     * Автоматично генерує унікальний ідентифікатор (UUID).
     *
     * @param filePath шлях до файлу або назва файлу.
     */
    public Photo(String filePath) {
        this.id = UUID.randomUUID().toString();
        this.filePath = filePath;
    }

    /**
     * Повертає строкове представлення об'єкта фотографії.
     * Зручно для логування та налагодження.
     * @return рядок у форматі "Photo[ID=..., Path=...]".
     */
    @Override
    public String toString() {
        return "Photo[ID=" + id + ", Path=" + filePath + "]";
    }
}