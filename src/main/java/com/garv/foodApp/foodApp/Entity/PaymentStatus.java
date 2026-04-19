package com.garv.foodApp.foodApp.Entity;

/**
 * Payment Status Enum
 * Represents the status of a payment transaction
 */
public enum PaymentStatus {
    PENDING, // Payment initiated but not completed
    COMPLETED, // Payment successful
    FAILED, // Payment failed
    REFUNDED // Payment refunded to customer
}
