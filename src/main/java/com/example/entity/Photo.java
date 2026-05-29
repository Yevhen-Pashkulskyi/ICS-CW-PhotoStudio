package com.example.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * Клас, що репрезентує окрему фотографію в межах замовлення.
 * Зберігає інформацію про фізичне розташування файлу.
 */
@Data
@NoArgsConstructor
public class Photo implements Serializable {

    private Long id;
    private String filePath;
    private Long orderId;

    public Photo(String filePath, Long orderId) {
        this.filePath = filePath;
        this.orderId = orderId;
    }

    public Photo(Long id, String filePath, Long orderId) {
        this.id = id;
        this.filePath = filePath;
        this.orderId = orderId;
    }
}