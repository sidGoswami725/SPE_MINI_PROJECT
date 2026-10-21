package com.spe.calculator;

import com.spe.calculator.service.CalculatorService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorServiceTest {

    private final CalculatorService service =
            new CalculatorService();

    @Test
    void testAddition() {
        assertEquals(8.0, service.add(5, 3));
    }

    @Test
    void testSubtraction() {
        assertEquals(2.0, service.subtract(5, 3));
    }

    @Test
    void testMultiplication() {
        assertEquals(15.0, service.multiply(5, 3));
    }

    @Test
    void testDivision() {
        assertEquals(2.0, service.divide(6, 3));
    }

    @Test
    void testDivisionByZero() {
        assertThrows(
            IllegalArgumentException.class,
            () -> service.divide(5, 0)
        );
    }
}
