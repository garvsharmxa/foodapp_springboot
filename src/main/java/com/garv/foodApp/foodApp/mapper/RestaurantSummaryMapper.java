package com.garv.foodApp.foodApp.mapper;

import com.garv.foodApp.foodApp.DTO.RestaurantSummaryDTO;
import com.garv.foodApp.foodApp.Entity.Restaurant;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RestaurantSummaryMapper {

    // Summary DTO mapping (for FoodItems)
    RestaurantSummaryDTO toRestaurantSummaryDTO(Restaurant restaurant);
    Restaurant toRestaurant(RestaurantSummaryDTO restaurantSummaryDTO);
    List<RestaurantSummaryDTO> toRestaurantSummaryDTOList(List<Restaurant> restaurants);

}
