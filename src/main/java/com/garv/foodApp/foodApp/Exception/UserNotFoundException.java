package com.garv.foodApp.foodApp.Exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(UUID id) {
        super("Sorry, we couldn’t find any user with ID: " + id);
    }

}
