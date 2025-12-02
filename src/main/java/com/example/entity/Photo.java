package com.example.entity;

import java.io.Serializable;
import java.util.UUID;

/**
 * Клас, що репрезентує окрему фотографію в межах замовлення.
 * Зберігає інформацію про унікальний ідентифікатор та фізичне розташування файлу.
 * Використовується у списку фотографій об'єкта Order (композиція).
 */
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
     * Отримує унікальний ідентифікатор фотографії.
     * @return рядок з ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Отримує шлях до файлу зображення.
     * @return рядок зі шляхом до файлу.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Встановлює або оновлює шлях до файлу.
     * Може використовуватися при переміщенні файлу або редагуванні шляху.
     *
     * @param filePath новий шлях до файлу.
     */
    public void setFilePath(String filePath) {
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

    /**
     * Встановлює ідентифікатор вручну.
     * Цей метод критично важливий для коректного відновлення зв'язків
     * при завантаженні даних із зовнішнього сховища (CSV, БД).
     *
     * @param id рядок з ідентифікатором.
     */
    public void setId(String id) {
        this.id = id;
    }
}