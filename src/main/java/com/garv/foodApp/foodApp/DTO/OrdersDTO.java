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
public class OrdersDTO {
    private Long id;
    private String status;
    private Long customerId;

    // Option 1: Use a simplified restaurant DTO to avoid circular reference
    private RestaurantSummaryDTO restaurant;

    // Option 2: Or just use restaurant ID
    // private Long restaurantId;

    private List<FoodItemsDTO> foodItems;
}
