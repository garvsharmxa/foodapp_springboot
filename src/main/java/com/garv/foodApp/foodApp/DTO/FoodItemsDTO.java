package com.garv.foodApp.foodApp.DTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodItemsDTO {

    private Long id;
    private String name;
    private int price;

    private RestaurantSummaryDTO restaurantSummaryDTO;
}
