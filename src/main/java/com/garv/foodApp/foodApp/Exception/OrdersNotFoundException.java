package com.garv.foodApp.foodApp.Exception;

public class OrdersNotFoundException extends RuntimeException {
    public OrdersNotFoundException(Long id) {
        super("Sorry, we couldn’t find any order with ID: " + id);
    }
}
