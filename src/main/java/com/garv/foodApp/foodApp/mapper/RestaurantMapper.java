package com.garv.foodApp.foodApp.mapper;

import com.garv.foodApp.foodApp.DTO.RestaurantDTO;
import com.garv.foodApp.foodApp.Entity.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {FoodItemMapper.class, OrderMapper.class})
public interface RestaurantMapper {
    RestaurantDTO toRestaurantDTO(Restaurant restaurant);
    Restaurant toRestaurant(RestaurantDTO restaurantDTO);
    void updateRestaurantFromDTO(RestaurantDTO restaurantDTO, @MappingTarget Restaurant restaurant);
    List<RestaurantDTO> toRestaurantDTOList(List<Restaurant> restaurants);
}