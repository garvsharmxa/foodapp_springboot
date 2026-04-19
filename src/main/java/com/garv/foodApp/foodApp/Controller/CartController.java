package com.garv.foodApp.foodApp.Controller;

import com.garv.foodApp.foodApp.DTO.*;
import com.garv.foodApp.foodApp.Entity.Cart;
import com.garv.foodApp.foodApp.Service.CartService;
import com.garv.foodApp.foodApp.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/user/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    /**
     * Get current user's cart summary
     * GET /user/cart
     */
    @GetMapping
    public ResponseEntity<CartSummaryDTO> getCart(Principal principal) {
        UUID userId = getUserIdFromPrincipal(principal);
        CartSummaryDTO summary = cartService.getCartSummary(userId);
        return ResponseEntity.ok(summary);
    }

    /**
     * Add item to cart
     * POST /user/cart/items
     */
    @PostMapping("/items")
    public ResponseEntity<CartSummaryDTO> addItemToCart(
            @RequestBody AddToCartRequest request,
            Principal principal) {
        try {
            UUID userId = getUserIdFromPrincipal(principal);
            cartService.addItemToCart(userId, request.getFoodItemId(), request.getQuantity());
            CartSummaryDTO summary = cartService.getCartSummary(userId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update cart item quantity
     * PUT /user/cart/items/{itemId}
     */
    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartSummaryDTO> updateCartItem(
            @PathVariable UUID itemId,
            @RequestBody UpdateCartItemRequest request,
            Principal principal) {
        try {
            UUID userId = getUserIdFromPrincipal(principal);
            cartService.updateCartItemQuantity(userId, itemId, request.getQuantity());
            CartSummaryDTO summary = cartService.getCartSummary(userId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Remove item from cart
     * DELETE /user/cart/items/{itemId}
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartSummaryDTO> removeCartItem(
            @PathVariable UUID itemId,
            Principal principal) {
        try {
            UUID userId = getUserIdFromPrincipal(principal);
            cartService.removeCartItem(userId, itemId);
            CartSummaryDTO summary = cartService.getCartSummary(userId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Clear entire cart
     * DELETE /user/cart
     */
    @DeleteMapping
    public ResponseEntity<Void> clearCart(Principal principal) {
        try {
            UUID userId = getUserIdFromPrincipal(principal);
            cartService.clearCart(userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Apply coupon to cart
     * POST /user/cart/coupon
     */
    @PostMapping("/coupon")
    public ResponseEntity<CartSummaryDTO> applyCoupon(
            @RequestBody ApplyCouponRequest request,
            Principal principal) {
        try {
            UUID userId = getUserIdFromPrincipal(principal);
            cartService.applyCoupon(userId, request.getCouponCode());
            CartSummaryDTO summary = cartService.getCartSummary(userId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Remove coupon from cart
     * DELETE /user/cart/coupon
     */
    @DeleteMapping("/coupon")
    public ResponseEntity<CartSummaryDTO> removeCoupon(Principal principal) {
        try {
            UUID userId = getUserIdFromPrincipal(principal);
            cartService.removeCoupon(userId);
            CartSummaryDTO summary = cartService.getCartSummary(userId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private UUID getUserIdFromPrincipal(Principal principal) {
        return userService.getCurrentUser().getId();
    }
}
