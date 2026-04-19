package com.garv.foodApp.foodApp.mapper;

import com.garv.foodApp.foodApp.DTO.FoodItemsDTO;
import com.garv.foodApp.foodApp.DTO.RestaurantSummaryDTO;
import com.garv.foodApp.foodApp.Entity.FoodItems;
import com.garv.foodApp.foodApp.Entity.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { RestaurantSummaryMapper.class })
public interface FoodItemMapper {

    @Mapping(target = "restaurantSummaryDTO", source = "restaurant")
    @Mapping(target = "id", source = "id")
    FoodItemsDTO toFoodItemDTO(FoodItems foodItem);

    @Mapping(target = "restaurant", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    FoodItems toFoodItem(FoodItemsDTO foodItemDTO);
}
