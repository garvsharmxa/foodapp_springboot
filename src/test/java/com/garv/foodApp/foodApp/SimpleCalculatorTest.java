package com.garv.foodApp.foodApp;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimpleCalculatorTest {

    @Test
    void onePlusTwoShouldEqualThree() {
        var calculator = new SimpleCalculator();
        assertEquals(3, calculator.add(1, 2)); // checks 1+2 = 3
        assertNotEquals(4, calculator.add(1, 2)); // checks 1+2 ≠ 4
        assertTrue(calculator.add(1, 2) == 3);
    }

    @Test
    void threePlusSevenShouldEqualTen() {
        var calculator = new SimpleCalculator();
        assertEquals(10, calculator.add(3, 7)); // checks 1+2 = 3
        assertNotEquals(4, calculator.add(3, 7)); // checks 1+2 ≠ 4
        assertTrue(calculator.add(3, 7) == 10);
    }

}
