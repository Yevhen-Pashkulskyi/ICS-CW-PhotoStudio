package com.example.entity;

import com.example.ui.util.Validate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhoneNumberTest {

    private Validate validate;

    @BeforeEach
    void setUp() {
        validate = new Validate();
    }

    // клас: 10 чисел (норма)
    @Test
    void testValidPhoneNumber() {
        assertTrue(validate.phoneValidate("0501234567"));
    }

    // клас: менше 10 чисел (замало)
    @Test
    void testTooShortPhoneNumber() {
        assertFalse(validate.phoneValidate("050123456"));
    }

    // клас: більше 10 чисел (забагато)
    @Test
    void testTooLongPhoneNumber() {
        assertFalse(validate.phoneValidate("05012345678"));
    }

    // клас: містить літери чи символи (не числа)
    @Test
    void testNonNumericPhoneNumber() {
        assertFalse(validate.phoneValidate("050123456a"));
    }
}