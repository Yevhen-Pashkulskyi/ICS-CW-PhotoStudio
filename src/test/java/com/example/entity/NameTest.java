package com.example.entity;

import com.example.ui.util.Validate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NameTest {

    @Test
    void testNameTooShort() {
        assertFalse(Validate.nameValidate("A")); // 1 літера — невалідно
    }

    @Test
    void testNameValid() {
        assertTrue(Validate.nameValidate("Олександр")); // Нормальне ім'я — валідно
    }

    @Test
    void testNameWithNumbers() {
        assertFalse(Validate.nameValidate("Іван123")); // Цифри в імені — невалідно
    }
}