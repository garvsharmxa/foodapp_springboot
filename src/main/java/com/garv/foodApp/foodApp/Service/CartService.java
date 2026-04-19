package com.garv.foodApp.foodApp.Service;

import com.garv.foodApp.foodApp.DTO.CartItemDTO;
import com.garv.foodApp.foodApp.DTO.CartSummaryDTO;
import com.garv.foodApp.foodApp.Entity.*;
import com.garv.foodApp.foodApp.Exception.CartRestaurantMismatchException;
import com.garv.foodApp.foodApp.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final FoodItemsRepository foodItemsRepository;
    private final CouponService couponService;

    /**
     * Get or create cart for user
     */
    @Transactional
    public Cart getOrCreateCart(UUID userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    /**
     * Add item to cart
     */
    @Transactional
    public Cart addItemToCart(UUID userId, UUID foodItemId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);
        FoodItems foodItem = foodItemsRepository.findById(foodItemId)
                .orElseThrow(() -> new RuntimeException("Food item not found: " + foodItemId));

        Restaurant itemRestaurant = foodItem.getRestaurant();

        // Check if cart is empty or belongs to the same restaurant
        if (cart.getRestaurant() == null) {
            cart.setRestaurant(itemRestaurant);
        } else if (!cart.getRestaurant().getId().equals(itemRestaurant.getId())) {
            throw new CartRestaurantMismatchException(
                    cart.getRestaurant().getName(),
                    itemRestaurant.getName());
        }

        // Check if item already exists in cart
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getFoodItem().getId().equals(foodItemId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Update quantity
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setPrice(foodItem.getPrice() * 1.0); // Update price in case it changed
        } else {
            // Add new item
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .foodItem(foodItem)
                    .quantity(quantity)
                    .price(foodItem.getPrice() * 1.0)
                    .build();
            cart.getItems().add(newItem);
        }

        // Recalculate totals
        cart.calculateTotals();
        return cartRepository.save(cart);
    }

    /**
     * Update cart item quantity
     */
    @Transactional
    public Cart updateCartItemQuantity(UUID userId, UUID cartItemId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + cartItemId));

        if (quantity <= 0) {
            cart.getItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
        }

        // If cart is empty, clear restaurant
        if (cart.getItems().isEmpty()) {
            cart.setRestaurant(null);
            cart.setCoupon(null);
        }

        cart.calculateTotals();
        return cartRepository.save(cart);
    }

    /**
     * Remove item from cart
     */
    @Transactional
    public Cart removeCartItem(UUID userId, UUID cartItemId) {
        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + cartItemId));

        cart.getItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        // If cart is empty, clear restaurant and coupon
        if (cart.getItems().isEmpty()) {
            cart.setRestaurant(null);
            cart.setCoupon(null);
        }

        cart.calculateTotals();
        return cartRepository.save(cart);
    }

    /**
     * Clear cart
     */
    @Transactional
    public void clearCart(UUID userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().clear();
        cart.setRestaurant(null);
        cart.setCoupon(null);
        cart.calculateTotals();
        cartRepository.save(cart);
    }

    /**
     * Apply coupon to cart
     */
    @Transactional
    public Cart applyCoupon(UUID userId, String couponCode) {
        Cart cart = getOrCreateCart(userId);

        if (cart.getRestaurant() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot apply coupon to empty cart");
        }

        // Calculate total before applying coupon
        Double totalAmount = cart.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        // Validate and get coupon
        Coupon coupon = couponService.validateCoupon(
                couponCode,
                totalAmount,
                cart.getRestaurant().getId());

        cart.setCoupon(coupon);
        cart.calculateTotals();
        return cartRepository.save(cart);
    }

    /**
     * Remove coupon from cart
     */
    @Transactional
    public Cart removeCoupon(UUID userId) {
        Cart cart = getOrCreateCart(userId);
        cart.setCoupon(null);
        cart.calculateTotals();
        return cartRepository.save(cart);
    }

    /**
     * Get cart summary
     */
    public CartSummaryDTO getCartSummary(UUID userId) {
        Cart cart = getOrCreateCart(userId);

        List<CartItemDTO> itemDTOs = cart.getItems().stream()
                .map(item -> CartItemDTO.builder()
                        .id(item.getId())
                        .foodItemId(item.getFoodItem().getId())
                        .foodItemName(item.getFoodItem().getName())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .totalPrice(item.getPrice() * item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return CartSummaryDTO.builder()
                .cartId(cart.getId())
                .userId(cart.getUser().getId())
                .restaurantId(cart.getRestaurant() != null ? cart.getRestaurant().getId() : null)
                .restaurantName(cart.getRestaurant() != null ? cart.getRestaurant().getName() : null)
                .items(itemDTOs)
                .itemCount(cart.getItems().size())
                .totalAmount(cart.getTotalAmount())
                .couponCode(cart.getCoupon() != null ? cart.getCoupon().getCode() : null)
                .discountAmount(cart.getDiscountAmount())
                .deliveryFee(0.0) // Takeaway - no delivery fee
                .finalAmount(cart.getFinalAmount())
                .build();
    }

    /**
     * Get cart by user ID
     */
    public Cart getCartByUserId(UUID userId) {
        return getOrCreateCart(userId);
    }
}
