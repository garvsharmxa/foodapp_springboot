package com.garv.foodApp.foodApp.Exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("Sorry, we couldn’t find any user with ID: " + id);
    }

}
