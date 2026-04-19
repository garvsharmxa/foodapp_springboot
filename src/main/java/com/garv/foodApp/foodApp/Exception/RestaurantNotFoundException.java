package com.garv.foodApp.foodApp.Exception;

import java.util.UUID;

public class RestaurantNotFoundException extends RuntimeException {
    public RestaurantNotFoundException(UUID id) {
        super("Sorry, we couldn’t find any restaurant with ID: " + id);
    }
}