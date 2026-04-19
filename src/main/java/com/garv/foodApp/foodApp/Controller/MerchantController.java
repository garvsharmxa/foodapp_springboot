package com.garv.foodApp.foodApp.Controller;

import com.garv.foodApp.foodApp.DTO.*;
import com.garv.foodApp.foodApp.Entity.*;
import com.garv.foodApp.foodApp.Service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/merchant")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MERCHANT')")
public class MerchantController {

    private final RestaurantService restaurantService;
    private final OrderServices orderService;
    private final FoodItemService foodItemService;
    private final UserService userService;
    private final DashboardService dashboardService;
    private final OrderTrackingService orderTrackingService;
    private final NotificationService notificationService;

    // ========== VENDOR PROFILE ==========

    /**
     * Get merchant's own restaurant profile
     * GET /merchant/vendor/profile
     */
    @GetMapping("/vendor/profile")
    public ResponseEntity<RestaurantDTO> getProfile() {
        Users user = userService.getCurrentUser();
        RestaurantDTO restaurant = restaurantService.getRestaurantByOwnerUserId(user.getId());
        return ResponseEntity.ok(restaurant);
    }

    /**
     * Update merchant's restaurant profile
     * PUT /merchant/vendor/profile
     */
    @PutMapping("/vendor/profile")
    public ResponseEntity<RestaurantDTO> updateProfile(@RequestBody RestaurantDTO vendorDTO) {
        Users user = userService.getCurrentUser();
        Restaurant restaurant = restaurantService.getRestaurantEntityByOwnerUserId(user.getId());
        RestaurantDTO updated = restaurantService.updateRestaurant(restaurant.getId(), vendorDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * Toggle restaurant open/close
     * PATCH /merchant/vendor/toggle-status
     */
    @PatchMapping("/vendor/toggle-status")
    public ResponseEntity<ApiResponse<RestaurantDTO>> toggleStatus() {
        Users user = userService.getCurrentUser();
        Restaurant restaurant = restaurantService.getRestaurantEntityByOwnerUserId(user.getId());
        RestaurantDTO updated = restaurantService.toggleRestaurantStatus(restaurant.getId());
        String status = updated.getIsOpen() != null && updated.getIsOpen() ? "OPEN" : "CLOSED";
        return ResponseEntity.ok(ApiResponse.success("Restaurant is now " + status, updated));
    }

    // ========== ORDERS ==========

    /**
     * Get all orders for merchant's restaurant
     * GET /merchant/orders
     */
    @GetMapping("/orders")
    public ResponseEntity<Page<OrderResponseDTO>> getMyOrders(Pageable pageable) {
        Users user = userService.getCurrentUser();
        Restaurant restaurant = restaurantService.getRestaurantEntityByOwnerUserId(user.getId());
        return ResponseEntity.ok(orderService.getOrdersByRestaurantId(restaurant.getId(), pageable));
    }

    /**
     * Get today's orders for merchant's restaurant
     * GET /merchant/orders/today
     */
    @GetMapping("/orders/today")
    public ResponseEntity<Page<OrderResponseDTO>> getTodayOrders(Pageable pageable) {
        Users user = userService.getCurrentUser();
        Restaurant restaurant = restaurantService.getRestaurantEntityByOwnerUserId(user.getId());
        return ResponseEntity.ok(orderService.getTodayOrdersByRestaurant(restaurant.getId(), pageable));
    }

    /**
     * Get orders filtered by status
     * GET /merchant/orders/status/{status}
     */
    @GetMapping("/orders/status/{status}")
    public ResponseEntity<Page<OrderResponseDTO>> getOrdersByStatus(@PathVariable OrderStatus status, Pageable pageable) {
        Users user = userService.getCurrentUser();
        Restaurant restaurant = restaurantService.getRestaurantEntityByOwnerUserId(user.getId());
        return ResponseEntity.ok(orderService.getOrdersByRestaurantAndStatus(restaurant.getId(), status, pageable));
    }

    /**
     * Get specific order details
     * GET /merchant/orders/{orderId}
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getOrderResponseById(orderId));
    }

    /**
     * Update order status (with WebSocket notification)
     * PATCH /merchant/orders/{orderId}/status?status=PREPARING
     */
    @PatchMapping("/orders/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderTrackingDTO>> updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestParam OrderStatus status,
            @RequestParam(required = false) String remarks) {
        Users user = userService.getCurrentUser();
        OrderTrackingDTO tracking = orderTrackingService.updateOrderStatus(
                orderId, status, user.getUsername(), remarks);
        return ResponseEntity.ok(ApiResponse.success("Order status updated to " + status, tracking));
    }

    // ========== MENU MANAGEMENT ==========

    /**
     * Get merchant's own menu items
     * GET /merchant/menu
     */
    @GetMapping("/menu")
    public ResponseEntity<List<FoodItems>> getMenu() {
        Users user = userService.getCurrentUser();
        Restaurant restaurant = restaurantService.getRestaurantEntityByOwnerUserId(user.getId());
        return ResponseEntity.ok(restaurantService.getFoodItemsByRestaurantId(restaurant.getId()));
    }

    /**
     * Add new food item to menu
     * POST /merchant/menu
     */
    @PostMapping("/menu")
    public ResponseEntity<FoodItems> addFoodItem(@RequestBody FoodItems foodItem) {
        Users user = userService.getCurrentUser();
        Restaurant restaurant = restaurantService.getRestaurantEntityByOwnerUserId(user.getId());
        foodItem.setRestaurant(restaurant);
        return ResponseEntity.ok(foodItemService.createFoodItem(foodItem));
    }

    /**
     * Update food item
     * PUT /merchant/menu/{itemId}
     */
    @PutMapping("/menu/{itemId}")
    public ResponseEntity<FoodItems> updateFoodItem(@PathVariable UUID itemId, @RequestBody FoodItems foodItem) {
        return ResponseEntity.ok(foodItemService.updateFoodItem(itemId, foodItem));
    }

    /**
     * Delete food item from menu
     * DELETE /merchant/menu/{itemId}
     */
    @DeleteMapping("/menu/{itemId}")
    public ResponseEntity<Void> deleteFoodItem(@PathVariable UUID itemId) {
        foodItemService.deleteFoodItem(itemId);
        return ResponseEntity.noContent().build();
    }

    // ========== DASHBOARD ==========

    /**
     * Get merchant dashboard statistics
     * GET /merchant/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        Users user = userService.getCurrentUser();
        Restaurant restaurant = restaurantService.getRestaurantEntityByOwnerUserId(user.getId());
        return ResponseEntity.ok(dashboardService.getMerchantDashboardStats(restaurant.getId()));
    }
}
