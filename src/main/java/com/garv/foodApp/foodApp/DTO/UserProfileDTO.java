package com.garv.foodApp.foodApp.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileDTO {
    private UUID id;

    @NotBlank(message = "Username cannot be blank")
    private String username;

    @Email(message = "Please provide a valid email format")
    private String email;
    private String role;
    private UUID locationId;
    private String locationName;
}
