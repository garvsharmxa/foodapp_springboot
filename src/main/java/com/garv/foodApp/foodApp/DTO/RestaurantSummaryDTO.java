package com.garv.foodApp.foodApp.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantSummaryDTO {
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
}
