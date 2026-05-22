package com.example.entity;

import com.example.ui.util.Validate;
import org.junit.jupiter.api.Test;

class PerformanceLoadTest {

    @Test
    void testValidationLoad() {
        Validate validate = new Validate();

        int numberOfRequests = 100000; // Кількість "запитів" (навантаження)

        long startTime = System.currentTimeMillis(); // Початок заміру часу

        for (int i = 0; i < numberOfRequests; i++) {
            // Імітуємо постійне введення даних користувачами
            validate.phoneValidate("0501234567");
            validate.nameValidate("Alexander");
        }

        long endTime = System.currentTimeMillis(); // Кінець заміру часу
        long totalTime = endTime - startTime; // Загальний час у мілісекундах

        // Рахуємо пропускну здатність (операцій за секунду)
        double throughput = (double) numberOfRequests / (totalTime / 1000.0);

        // Виводимо результати для звіту
        System.out.println("=== РЕЗУЛЬТАТИ НАВАНТАЖУВАЛЬНОГО ТЕСТУ ===");
        System.out.println("Кількість оброблених запитів: " + numberOfRequests);
        System.out.println("Загальний час виконання: " + totalTime + " мс");
        System.out.println("Пропускна здатність (Throughput): " + String.format("%.2f", throughput) + " оп/сек");
    }
}