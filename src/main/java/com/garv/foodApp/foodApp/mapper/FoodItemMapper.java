package com.garv.foodApp.foodApp.mapper;

import com.garv.foodApp.foodApp.DTO.FoodItemsDTO;
import com.garv.foodApp.foodApp.DTO.RestaurantSummaryDTO;
import com.garv.foodApp.foodApp.Entity.FoodItems;
import com.garv.foodApp.foodApp.Entity.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FoodItemMapper {

    @Mapping(target = "restaurantSummaryDTO", source = "restaurant")
    FoodItemsDTO toFoodItemDTO(FoodItems foodItem);

    @Mapping(target = "restaurant", ignore = true)
    FoodItems toFoodItem(FoodItemsDTO foodItemDTO);

    // Map Restaurant → RestaurantSummaryDTO
    RestaurantSummaryDTO toRestaurantSummaryDTO(Restaurant restaurant);
}

