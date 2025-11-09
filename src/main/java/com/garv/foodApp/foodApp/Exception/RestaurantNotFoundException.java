package com.garv.foodApp.foodApp.Exception;

public class RestaurantNotFoundException extends RuntimeException {
    public RestaurantNotFoundException(Long id) {
        super("Sorry, we couldn’t find any restaurant with ID: " + id);
    }
}