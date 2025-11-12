package com.garv.foodApp.foodApp.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String message;
    private boolean success;
    private String accessToken;
    private String refreshToken;
    
    // Convenience constructor for simple responses without tokens
    public AuthResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
}
