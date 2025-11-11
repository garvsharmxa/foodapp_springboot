package com.garv.foodApp.foodApp.Controller;

import com.garv.foodApp.foodApp.DTO.CartDTO;
import com.garv.foodApp.foodApp.Service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{customerId}")
    public ResponseEntity<CartDTO> getCart(@PathVariable Long customerId) {
        CartDTO cart = cartService.getOrCreateCart(customerId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/{customerId}/items")
    public ResponseEntity<CartDTO> addItemToCart(
            @PathVariable Long customerId,
            @RequestParam Long foodItemId,
            @RequestParam Integer quantity) {
        CartDTO cart = cartService.addItemToCart(customerId, foodItemId, quantity);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/{customerId}/items/{cartItemId}")
    public ResponseEntity<CartDTO> removeItemFromCart(
            @PathVariable Long customerId,
            @PathVariable Long cartItemId) {
        CartDTO cart = cartService.removeItemFromCart(customerId, cartItemId);
        return ResponseEntity.ok(cart);
    }

    @PatchMapping("/{customerId}/items/{cartItemId}")
    public ResponseEntity<CartDTO> updateItemQuantity(
            @PathVariable Long customerId,
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {
        CartDTO cart = cartService.updateItemQuantity(customerId, cartItemId, quantity);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long customerId) {
        cartService.clearCart(customerId);
        return ResponseEntity.noContent().build();
    }
}
