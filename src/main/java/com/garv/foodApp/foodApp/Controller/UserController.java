package com.garv.foodApp.foodApp.Controller;

import com.garv.foodApp.foodApp.DTO.*;
import com.garv.foodApp.foodApp.Entity.FoodItems;
import com.garv.foodApp.foodApp.Entity.Users;
import com.garv.foodApp.foodApp.Service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class UserController {

    private final LocationService locationService;
    private final RestaurantService restaurantService;
    private final OrderServices ordersService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final OrderTrackingService orderTrackingService;

    // ========== PROFILE ==========

    /**
     * Get current user's profile
     * GET /user/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getProfile() {
        UserProfileDTO profile = userService.getCurrentUserProfile();
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved", profile));
    }

    /**
     * Update current user's profile
     * PUT /user/profile
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDTO>> updateProfile(@Valid @RequestBody UserProfileDTO profileDTO) {
        UserProfileDTO updated = userService.updateProfile(
                profileDTO.getUsername(),
                profileDTO.getLocationId()
        );
        return ResponseEntity.ok(ApiResponse.success("Profile updated", updated));
    }

    // ========== LOCATIONS ==========

    /**
     * Get all available locations
     * GET /user/locations
     */
    @GetMapping("/locations")
    public ResponseEntity<List<LocationDTO>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    // ========== RESTAURANTS / VENDORS ==========

    /**
     * Get vendors/restaurants by location
     */
    @GetMapping("/vendors")
    public ResponseEntity<Page<RestaurantDTO>> getVendorsByLocation(Pageable pageable) {
        Users user = userService.getCurrentUser();
        if (user.getLocation() == null) {
            throw new RuntimeException("User is not assigned to any location.");
        }
        return ResponseEntity.ok(restaurantService.getRestaurantsByLocation(user.getLocation().getId(), pageable));
    }

    /**
     * Get vendor menu (food items)
     * GET /user/vendors/{vendorId}/menu
     */
    @GetMapping("/vendors/{vendorId}/menu")
    public ResponseEntity<List<FoodItems>> getMenu(@PathVariable UUID vendorId) {
        return ResponseEntity.ok(restaurantService.getFoodItemsByRestaurantId(vendorId));
    }

    /**
     * Search restaurants by name or category
     * GET /user/restaurants/search?q=pizza
     */
    @GetMapping("/restaurants/search")
    public ResponseEntity<Page<RestaurantDTO>> searchRestaurants(@RequestParam String q, Pageable pageable) {
        return ResponseEntity.ok(restaurantService.searchRestaurants(q, pageable));
    }

    /**
     * Get restaurant details
     * GET /user/restaurants/{id}
     */
    @GetMapping("/restaurants/{id}")
    public ResponseEntity<RestaurantDTO> getRestaurantById(@PathVariable UUID id) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(id));
    }

    // ========== ORDERS ==========

    /**
     * Place order from cart
     * POST /user/orders
     */
    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> placeOrderFromCart() {
        Users user = userService.getCurrentUser();
        OrderResponseDTO order = ordersService.placeOrderFromCart(user.getId());

        // Send WebSocket notification to merchant
        if (order.getRestaurantId() != null) {
            notificationService.notifyNewOrder(order.getRestaurantId(), order);
        }

        return ResponseEntity.ok(ApiResponse.success("Order placed successfully", order));
    }

    /**
     * Place order directly (without cart)
     * POST /user/orders/direct
     */
    @PostMapping("/orders/direct")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> placeOrderDirect(@Valid @RequestBody PlaceOrderRequest request) {
        Users user = userService.getCurrentUser();
        OrderResponseDTO order = ordersService.placeOrder(user.getId(), request);

        // Send WebSocket notification to merchant
        if (order.getRestaurantId() != null) {
            notificationService.notifyNewOrder(order.getRestaurantId(), order);
        }

        return ResponseEntity.ok(ApiResponse.success("Order placed successfully", order));
    }

    /**
     * Get all orders for current user (order history)
     * GET /user/orders
     */
    @GetMapping("/orders")
    public ResponseEntity<Page<OrderResponseDTO>> getMyOrders(Pageable pageable) {
        Users user = userService.getCurrentUser();
        return ResponseEntity.ok(ordersService.getOrdersByUserId(user.getId(), pageable));
    }

    /**
     * Get specific order details
     * GET /user/orders/{orderId}
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable UUID orderId) {
        return ResponseEntity.ok(ordersService.getOrderResponseById(orderId));
    }

    /**
     * Cancel an order
     * PUT /user/orders/{orderId}/cancel
     */
    @PutMapping("/orders/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> cancelOrder(
            @PathVariable UUID orderId,
            @RequestParam(required = false, defaultValue = "Customer requested cancellation") String reason) {
        Users user = userService.getCurrentUser();
        OrderResponseDTO cancelled = ordersService.cancelOrder(orderId, user.getId(), reason);

        // Notify merchant of cancellation
        if (cancelled.getRestaurantId() != null) {
            OrderTrackingDTO tracking = OrderTrackingDTO.builder()
                    .orderId(orderId)
                    .status(com.garv.foodApp.foodApp.Entity.OrderStatus.CANCELLED)
                    .statusMessage("Order cancelled by customer: " + reason)
                    .build();
            notificationService.notifyMerchantOrderUpdate(cancelled.getRestaurantId(), tracking);
        }

        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", cancelled));
    }

    /**
     * Get order status tracking history
     * GET /user/orders/{orderId}/history
     */
    @GetMapping("/orders/{orderId}/history")
    public ResponseEntity<?> getOrderHistory(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderTrackingService.getOrderStatusHistory(orderId));
    }
}
