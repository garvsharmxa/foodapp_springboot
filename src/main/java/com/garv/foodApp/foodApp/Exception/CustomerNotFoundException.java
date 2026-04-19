package com.garv.foodApp.foodApp.Exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(Long id) {
      super("Sorry, we couldn’t find any customer with ID: " + id);
    }
}
