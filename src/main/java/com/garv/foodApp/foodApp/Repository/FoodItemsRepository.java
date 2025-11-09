package com.garv.foodApp.foodApp.Repository;

import com.garv.foodApp.foodApp.Entity.FoodItems;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodItemsRepository extends JpaRepository<FoodItems, Long> {
    List<FoodItems> findByRestaurantId(Long restaurantId);
}