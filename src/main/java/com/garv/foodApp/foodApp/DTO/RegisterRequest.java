package com.garv.foodApp.foodApp.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String userName;
    private String email;
    private String password;
    private String role; // CUSTOMER, ADMIN, RESTAURANT_OWNER, DELIVERY_PERSON
}
