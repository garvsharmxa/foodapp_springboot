package com.garv.foodApp.foodApp.Exception;

import java.util.UUID;

public class FoodItemsNotFoundException extends RuntimeException {
    public FoodItemsNotFoundException(UUID id) {
        super(
                "Could not find food items with id " + id + ".");
    }
}
