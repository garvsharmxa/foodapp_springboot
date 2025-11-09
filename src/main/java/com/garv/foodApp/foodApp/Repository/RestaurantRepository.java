package com.garv.foodApp.foodApp.Repository;

import com.garv.foodApp.foodApp.Entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}