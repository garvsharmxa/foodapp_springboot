package com.garv.foodApp.foodApp.Repository;

import com.garv.foodApp.foodApp.Entity.Cart;
import com.garv.foodApp.foodApp.Entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    List<CartItem> findByCart(Cart cart);

    void deleteByCart(Cart cart);
}
