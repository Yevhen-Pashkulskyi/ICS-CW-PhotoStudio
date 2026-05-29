package com.example.entity;

import com.example.ui.util.Validate;
import org.junit.jupiter.api.Test;

class PerformanceLoadTest {

    @Test
    void testValidationLoad() {
        int numberOfRequests = 100000;
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numberOfRequests; i++) {
            Validate.phoneValidate("0501234567");
            Validate.nameValidate("Владислав");
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double throughput = (double) numberOfRequests / (totalTime / 1000.0);

        System.out.println("=== РЕЗУЛЬТАТИ НАВАНТАЖУВАЛЬНОГО ТЕСТУ ===");
        System.out.println("Кількість оброблених запитів: " + numberOfRequests);
        System.out.println("Загальний час виконання: " + totalTime + " мс");
        System.out.println("Пропускна здатність (Throughput): " + String.format("%.2f", throughput) + " оп/сек");
    }
}