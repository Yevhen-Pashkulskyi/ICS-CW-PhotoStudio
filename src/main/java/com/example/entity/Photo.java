package com.example.entity;

import java.io.Serializable;
import java.util.UUID;

/**
 * Клас, що описує файл фотографії.
 * Реалізує Serializable для збереження у складі Order.
 */
public class Photo implements Serializable {
    private String id;
    private String filePath;

    /**
     * Конструктор для створення об'єкта Photo.
     * ID генерується автоматично.
     * @param filePath шлях до файлу.
     */
    public Photo(String filePath) {
        this.id = UUID.randomUUID().toString();
        this.filePath = filePath;
    }

    public String getId() {
        return id;
    }

    public String getFilePath() {
        return filePath;
    }

    /**
     * Дозволяє оновити шлях до файлу (напр., після редагування).
     * @param filePath новий шлях до файлу.
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "Photo[ID=" + id + ", Path=" + filePath + "]";
    }
}
