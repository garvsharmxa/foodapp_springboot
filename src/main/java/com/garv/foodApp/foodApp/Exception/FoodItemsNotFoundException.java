package com.garv.foodApp.foodApp.Exception;

public class FoodItemsNotFoundException extends RuntimeException {
    public FoodItemsNotFoundException(Long id) {
        super(
                "Could not find food items with id " + id + "."
        );
    }
}
