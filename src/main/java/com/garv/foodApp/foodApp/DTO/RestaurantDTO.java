package com.garv.foodApp.foodApp.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDTO {
    private UUID id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String ownerName;
    private Double rating;
    private String imageUrl;
    private Boolean isOpen;
    private String category;
    private UUID locationId;

    private List<FoodItemsDTO> menu;
}
