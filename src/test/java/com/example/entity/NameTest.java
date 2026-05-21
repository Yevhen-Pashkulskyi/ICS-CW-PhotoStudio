package com.example.entity;

import com.example.ui.util.Validate;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NameTest {
    private Validate validate;

    @BeforeEach
    void setUp() {
        validate = new Validate();
    }

    // 1 символ - замале
    @Test
    void testNameMinInvalid() {
        assertFalse(validate.nameValidate("A"));
    }

    // 2 символи - норм
    @Test
    void testNameMinValid() {
        assertTrue(validate.nameValidate("An"));
    }

    // 20 символів - норм
    @Test
    void testNameMaxValid() {
        assertTrue(validate.nameValidate("ThisNameVeryVeryLong"));
    }

    // 21 символ - завелике
    @Test
    void testNameMaxInvalid() {
        assertFalse(validate.nameValidate("ThisNameVeryVeryLongg"));
    }
}
