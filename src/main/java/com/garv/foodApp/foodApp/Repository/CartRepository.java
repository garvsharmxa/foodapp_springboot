package com.garv.foodApp.foodApp.Repository;

import com.garv.foodApp.foodApp.Entity.Cart;
import com.garv.foodApp.foodApp.Entity.Restaurant;
import com.garv.foodApp.foodApp.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {

    Optional<Cart> findByUser(Users user);

    Optional<Cart> findByUserId(UUID userId);

    Optional<Cart> findByUserAndRestaurant(Users user, Restaurant restaurant);
}
