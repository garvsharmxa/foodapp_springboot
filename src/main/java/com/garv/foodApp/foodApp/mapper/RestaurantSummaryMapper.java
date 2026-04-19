package com.garv.foodApp.foodApp.mapper;

import com.garv.foodApp.foodApp.DTO.RestaurantSummaryDTO;
import com.garv.foodApp.foodApp.Entity.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RestaurantSummaryMapper {

    RestaurantSummaryDTO toRestaurantSummaryDTO(Restaurant restaurant);

    @Mapping(target = "foodItems", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "ownerUser", ignore = true)
    Restaurant toRestaurant(RestaurantSummaryDTO restaurantSummaryDTO);

    List<RestaurantSummaryDTO> toRestaurantSummaryDTOList(List<Restaurant> restaurants);
}
