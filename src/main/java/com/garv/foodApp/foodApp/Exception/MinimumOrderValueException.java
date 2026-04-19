package com.garv.foodApp.foodApp.Exception;

public class MinimumOrderValueException extends RuntimeException {
    public MinimumOrderValueException(Double required, Double actual) {
        super(String.format("Minimum order value of ₹%.2f required. Current cart total: ₹%.2f", required, actual));
    }
}
