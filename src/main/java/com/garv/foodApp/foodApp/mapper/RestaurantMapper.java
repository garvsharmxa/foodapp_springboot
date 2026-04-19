package com.garv.foodApp.foodApp.mapper;

import com.garv.foodApp.foodApp.DTO.RestaurantDTO;
import com.garv.foodApp.foodApp.Entity.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = { FoodItemMapper.class, OrderMapper.class, RestaurantSummaryMapper.class })
public interface RestaurantMapper {

    @Mapping(source = "location.id", target = "locationId")
    @Mapping(source = "foodItems", target = "menu")
    RestaurantDTO toRestaurantDTO(Restaurant restaurant);

    @Mapping(target = "location", ignore = true)
    @Mapping(target = "foodItems", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "deliveryRadius", ignore = true)
    @Mapping(target = "minimumOrderValue", ignore = true)
    @Mapping(target = "ownerUser", ignore = true)
    Restaurant toRestaurant(RestaurantDTO restaurantDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "foodItems", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "deliveryRadius", ignore = true)
    @Mapping(target = "minimumOrderValue", ignore = true)
    @Mapping(target = "ownerUser", ignore = true)
    void updateRestaurantFromDTO(RestaurantDTO restaurantDTO, @MappingTarget Restaurant restaurant);

    List<RestaurantDTO> toRestaurantDTOList(List<Restaurant> restaurants);
}