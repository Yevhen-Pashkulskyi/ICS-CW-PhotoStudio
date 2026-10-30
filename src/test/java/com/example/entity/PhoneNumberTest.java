package com.example.entity;

import com.example.ui.util.Validate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PhoneNumberTest {

    @Test
    void testValidPhoneNumber() {
        assertTrue(Validate.phoneValidate("380501234567")); // Рівно 10 цифр
    }

    @Test
    void testTooShortPhoneNumber() {
        assertFalse(Validate.phoneValidate("1")); // Менше 1 цифри
    }

    @Test
    void testContainsLetters() {
        assertFalse(Validate.phoneValidate("38050123456a")); // Наявність літери
    }
}