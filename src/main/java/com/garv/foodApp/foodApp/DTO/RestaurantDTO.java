package com.garv.foodApp.foodApp.DTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDTO {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String ownerName;
    private Double rating;
    private Double deliveryRadius;
    private Double minimumOrderValue;
    private String imageUrl;
    private Boolean isOpen;


    private List<FoodItemsDTO> menu;

    // For avoiding circular references, you might want to exclude orders here
    // or create a simplified version
    // private List<OrdersDTO> orders; // Remove this to break circular reference
}

