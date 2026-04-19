package com.garv.foodApp.foodApp.Entity;

/**
 * Order Status Enum
 * Represents the lifecycle of an order in a food takeaway application
 */
public enum OrderStatus {
    PLACED, // Order placed, payment pending or completed
    CONFIRMED, // Order confirmed by restaurant
    PREPARING, // Restaurant is preparing the order
    READY, // Order is ready for pickup
    PICKED_UP, // Customer has picked up the order
    COMPLETED, // Order completed successfully
    CANCELLED // Order cancelled by customer or restaurant
}
