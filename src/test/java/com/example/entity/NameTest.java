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

    // 1 літера - замале
    @Test
    void testNameLength_1() {
        assertFalse(validate.nameValidate("A"));
    }

    // 2 літери - норм
    @Test
    void testNameLength_2() {
        assertTrue(validate.nameValidate("An"));
    }

    // 3 літери - норм
    @Test
    void testNameLength_3() {
        assertTrue(validate.nameValidate("Ann"));
    }

    // 19 літер - норм
    @Test
    void testNameLength_19() {
        assertTrue(validate.nameValidate("ThisNameVeryVeryLon"));
    }

    // 20 літер - норм
    @Test
    void testNameLength_20() {
        assertTrue(validate.nameValidate("ThisNameVeryVeryLong"));
    }

    // 21 літера - завелике
    @Test
    void testNameLength_21() {
        assertFalse(validate.nameValidate("ThisNameVeryVeryLongg"));
    }
}
