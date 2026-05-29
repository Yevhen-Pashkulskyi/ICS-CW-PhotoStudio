package com.example.entity;

import com.example.ui.util.Validate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PhoneNumberTest {

    @Test
    void testValidPhoneNumber() {
        assertTrue(Validate.phoneValidate("0501234567")); // Рівно 10 цифр
    }

    @Test
    void testTooShortPhoneNumber() {
        assertFalse(Validate.phoneValidate("05012345")); // Менше 10 цифр
    }

    @Test
    void testContainsLetters() {
        assertFalse(Validate.phoneValidate("050123456a")); // Наявність літери
    }
}