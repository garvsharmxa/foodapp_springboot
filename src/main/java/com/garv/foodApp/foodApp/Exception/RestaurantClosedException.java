package com.garv.foodApp.foodApp.Exception;

public class RestaurantClosedException extends RuntimeException {
    public RestaurantClosedException(String restaurantName) {
        super("Restaurant '" + restaurantName + "' is currently closed. Please try again later.");
    }
}
