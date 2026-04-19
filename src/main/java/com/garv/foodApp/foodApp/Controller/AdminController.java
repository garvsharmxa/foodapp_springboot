package com.garv.foodApp.foodApp.Controller;

import com.garv.foodApp.foodApp.DTO.*;
import com.garv.foodApp.foodApp.Service.*;
import com.garv.foodApp.foodApp.Entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final LocationService locationService;
    private final RestaurantService restaurantService;
    private final OrderServices orderServices;
    private final FoodItemService foodItemService;
    private final UserService userService;
    private final CouponService couponService;
    private final DashboardService dashboardService;
    private final OrderTrackingService orderTrackingService;

    // ========== DASHBOARD ==========

    /**
     * Get admin dashboard statistics
     * GET /admin/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        return ResponseEntity.ok(dashboardService.getAdminDashboardStats());
    }

    // ========== LOCATIONS ==========

    @GetMapping("/locations")
    public ResponseEntity<List<LocationDTO>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @PostMapping("/locations")
    public ResponseEntity<LocationDTO> createLocation(@RequestBody LocationDTO locationDTO) {
        return ResponseEntity.ok(locationService.createLocation(locationDTO));
    }

    @PutMapping("/locations/{id}")
    public ResponseEntity<LocationDTO> updateLocation(@PathVariable UUID id, @RequestBody LocationDTO locationDTO) {
        return ResponseEntity.ok(locationService.updateLocation(id, locationDTO));
    }

    @DeleteMapping("/locations/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable UUID id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }

    // ========== VENDORS / RESTAURANTS ==========

    @GetMapping("/vendors")
    public ResponseEntity<Page<RestaurantDTO>> getAllVendors(Pageable pageable) {
        return ResponseEntity.ok(restaurantService.getAllRestaurants(pageable));
    }

    @PostMapping("/vendors")
    public ResponseEntity<RestaurantDTO> createVendor(@RequestBody RestaurantDTO vendorDTO) {
        return ResponseEntity.ok(restaurantService.createRestaurant(vendorDTO));
    }

    @PutMapping("/vendors/{id}")
    public ResponseEntity<RestaurantDTO> updateVendor(@PathVariable UUID id, @RequestBody RestaurantDTO vendorDTO) {
        return ResponseEntity.ok(restaurantService.updateRestaurant(id, vendorDTO));
    }

    @DeleteMapping("/vendors/{id}")
    public ResponseEntity<Void> deleteVendor(@PathVariable UUID id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Assign a merchant user to a restaurant
     * POST /admin/vendors/{restaurantId}/assign-owner?userId=xxx
     */
    @PostMapping("/vendors/{restaurantId}/assign-owner")
    public ResponseEntity<ApiResponse<RestaurantDTO>> assignOwner(
            @PathVariable UUID restaurantId,
            @RequestParam UUID userId) {
        Users owner = userService.getUserById(userId);
        RestaurantDTO updated = restaurantService.assignOwner(restaurantId, owner);
        return ResponseEntity.ok(ApiResponse.success("Owner assigned successfully", updated));
    }

    // Alias endpoints for consistency
    @GetMapping("/restaurants")
    public ResponseEntity<Page<RestaurantDTO>> getAllRestaurants(Pageable pageable) {
        return getAllVendors(pageable);
    }

    @PostMapping("/restaurants")
    public ResponseEntity<RestaurantDTO> createRestaurant(@RequestBody RestaurantDTO restaurantDTO) {
        return createVendor(restaurantDTO);
    }

    @PutMapping("/restaurants/{id}")
    public ResponseEntity<RestaurantDTO> updateRestaurant(@PathVariable UUID id,
            @RequestBody RestaurantDTO restaurantDTO) {
        return updateVendor(id, restaurantDTO);
    }

    @DeleteMapping("/restaurants/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable UUID id) {
        return deleteVendor(id);
    }

    // ========== FOOD ITEMS ==========

    @GetMapping("/food-items")
    public ResponseEntity<List<FoodItems>> getAllFoodItems() {
        return ResponseEntity.ok(foodItemService.getAllFoodItems());
    }

    @PostMapping("/food-items")
    public ResponseEntity<FoodItems> createFoodItem(@RequestBody FoodItems foodItem) {
        return ResponseEntity.ok(foodItemService.createFoodItem(foodItem));
    }

    @PutMapping("/food-items/{id}")
    public ResponseEntity<FoodItems> updateFoodItem(@PathVariable UUID id, @RequestBody FoodItems foodItem) {
        return ResponseEntity.ok(foodItemService.updateFoodItem(id, foodItem));
    }

    @DeleteMapping("/food-items/{id}")
    public ResponseEntity<Void> deleteFoodItem(@PathVariable UUID id) {
        foodItemService.deleteFoodItem(id);
        return ResponseEntity.noContent().build();
    }

    // ========== ORDERS ==========

    @GetMapping("/orders")
    public ResponseEntity<Page<OrderResponseDTO>> getAllOrders(Pageable pageable) {
        return ResponseEntity.ok(orderServices.getAllOrdersDTO(pageable));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderServices.getOrderResponseById(id));
    }

    /**
     * Filter orders by status
     * GET /admin/orders/status/{status}
     */
    @GetMapping("/orders/status/{status}")
    public ResponseEntity<Page<OrderResponseDTO>> getOrdersByStatus(@PathVariable OrderStatus status, Pageable pageable) {
        return ResponseEntity.ok(orderServices.getOrdersByStatus(status, pageable));
    }

    /**
     * Update order status (admin)
     * PATCH /admin/orders/{id}/status?status=CONFIRMED
     */
    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<OrderTrackingDTO>> updateOrderStatus(
            @PathVariable UUID id,
            @RequestParam OrderStatus status,
            @RequestParam(required = false) String remarks) {
        OrderTrackingDTO tracking = orderTrackingService.updateOrderStatus(id, status, "Admin", remarks);
        return ResponseEntity.ok(ApiResponse.success("Order status updated", tracking));
    }

    // ========== USERS ==========

    @GetMapping("/users")
    public ResponseEntity<List<Users>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // ========== COUPONS ==========

    @GetMapping("/coupons")
    public ResponseEntity<List<CouponDTO>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    @GetMapping("/coupons/{id}")
    public ResponseEntity<CouponDTO> getCouponById(@PathVariable UUID id) {
        return ResponseEntity.ok(couponService.convertToCouponDTO(couponService.getCouponById(id)));
    }

    @PostMapping("/coupons")
    public ResponseEntity<ApiResponse<CouponDTO>> createCoupon(@RequestBody CouponDTO couponDTO) {
        var coupon = couponService.createCoupon(couponDTO);
        return ResponseEntity.ok(ApiResponse.success("Coupon created", couponService.convertToCouponDTO(coupon)));
    }

    @PutMapping("/coupons/{id}")
    public ResponseEntity<ApiResponse<CouponDTO>> updateCoupon(@PathVariable UUID id, @RequestBody CouponDTO couponDTO) {
        var coupon = couponService.updateCoupon(id, couponDTO);
        return ResponseEntity.ok(ApiResponse.success("Coupon updated", couponService.convertToCouponDTO(coupon)));
    }

    @DeleteMapping("/coupons/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable UUID id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }
}
