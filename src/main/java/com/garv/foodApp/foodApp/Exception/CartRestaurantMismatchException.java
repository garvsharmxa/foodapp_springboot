package com.garv.foodApp.foodApp.Exception;

public class CartRestaurantMismatchException extends RuntimeException {
    public CartRestaurantMismatchException(String currentRestaurant, String newRestaurant) {
        super(String.format(
                "Cannot add items from '%s'. Your cart contains items from '%s'. Please clear your cart first.",
                newRestaurant, currentRestaurant));
    }
}
