package com.garv.foodApp.foodApp.Repository;

import com.garv.foodApp.foodApp.Entity.FoodItems;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FoodItemsRepository extends JpaRepository<FoodItems, UUID> {
    List<FoodItems> findByRestaurantId(UUID restaurantId);
}