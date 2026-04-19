package com.garv.foodApp.foodApp.Exception;

import java.util.UUID;

public class OrdersNotFoundException extends RuntimeException {
    public OrdersNotFoundException(UUID id) {
        super("Sorry, we couldn’t find any order with ID: " + id);
    }
}
