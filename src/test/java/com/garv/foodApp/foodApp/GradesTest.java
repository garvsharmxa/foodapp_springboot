package com.garv.foodApp.foodApp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GradesTest {

    @Test
    void fiftyNinetyNineShouldReturnF() {
        var grades = new Grades();
        assertEquals('F',grades.determineLetterGrade(59));
    }

    @Test
    void sixtyNinetyNineShouldReturnD() {
        var grades = new Grades();
        assertEquals('D',grades.determineLetterGrade(69));
    }

    @Test
    void seventyNinetyNineShouldReturnC() {
        var grades = new Grades();
        assertEquals('C',grades.determineLetterGrade(79));
    }

    @Test
    void EightyNinetyNineShouldReturnB() {
        var grades = new Grades();
        assertEquals('B',grades.determineLetterGrade(89));
    }

    @Test
    void NinetyNinetyNineShouldReturnA() {
        var grades = new Grades();
        assertEquals('A',grades.determineLetterGrade(99));
    }

    @Test
    void eightyShouldReturnB() {
        var grades = new Grades();
        assertEquals('B',grades.determineLetterGrade(80));
    }

    @Test
    void zeroShouldReturnF() {
        var grades = new Grades();
        assertEquals('F', grades.determineLetterGrade(0));
    }

    @Test
    void sixtyShouldReturnC_IncorrectTest() {
        var grades = new Grades();
        // This will fail because 60 actually returns 'D', not 'C'
        assertEquals('C', grades.determineLetterGrade(60));
    }

    @Test
    void eightyFiveShouldReturnB() {
        var grades = new Grades();
        assertEquals('B', grades.determineLetterGrade(85));
    }

    @Test
    void hundredShouldReturnA() {
        var grades = new Grades();
        assertEquals('A', grades.determineLetterGrade(100));
    }

    @Test
    void seventyShouldReturnD_IncorrectTest() {
        var grades = new Grades();
        // This will fail because 70 actually returns 'C', not 'D'
        assertEquals('D', grades.determineLetterGrade(70));
    }

    @Test
    void negativeGradeShouldThrowException() {
        var grades = new Grades();
        assertThrows(IllegalArgumentException.class, () -> grades.determineLetterGrade(-5));
    }

}