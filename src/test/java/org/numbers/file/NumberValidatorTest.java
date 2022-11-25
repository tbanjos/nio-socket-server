package org.numbers.file;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumberValidatorTest {

    @Test
    void numberIsValid() {
        var result = new NumberValidator().isValid("123456789");
        assertTrue(result);
    }

    @Test
    void shortNumberIsNotValid() {
        var result = new NumberValidator().isValid("123");
        assertFalse(result);
    }

    @Test
    void longNumberIsNotValid() {
        var result = new NumberValidator().isValid("012345678910");
        assertFalse(result);
    }

    @Test
    void wordIsNotValid() {
        var result = new NumberValidator().isValid("wrong");
        assertFalse(result);
    }
}