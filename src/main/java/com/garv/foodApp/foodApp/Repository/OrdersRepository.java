package com.garv.foodApp.foodApp.Repository;

import com.garv.foodApp.foodApp.Entity.Orders;
import com.garv.foodApp.foodApp.Entity.OrderStatus;
import com.garv.foodApp.foodApp.Entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrdersRepository extends JpaRepository<Orders, UUID> {

    Page<Orders> findByUserId(UUID userId, Pageable pageable);

    Page<Orders> findByUserIdOrderByOrderDateDesc(UUID userId, Pageable pageable);

    Page<Orders> findByStatus(OrderStatus status, Pageable pageable);

    Optional<Orders> findByRazorpayOrderId(String razorpayOrderId);

    Page<Orders> findByRestaurantId(UUID restaurantId, Pageable pageable);

    Page<Orders> findByRestaurantIdOrderByOrderDateDesc(UUID restaurantId, Pageable pageable);

    Page<Orders> findByRestaurantIdAndStatus(UUID restaurantId, OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Orders o WHERE o.restaurant.id = :restaurantId AND o.orderDate >= :startOfDay AND o.orderDate < :endOfDay ORDER BY o.orderDate DESC")
    Page<Orders> findTodayOrdersByRestaurant(@Param("restaurantId") UUID restaurantId,
                                              @Param("startOfDay") LocalDateTime startOfDay,
                                              @Param("endOfDay") LocalDateTime endOfDay,
                                              Pageable pageable);

    @Query("SELECT COUNT(o) FROM Orders o WHERE o.restaurant.id = :restaurantId")
    Long countByRestaurantId(@Param("restaurantId") UUID restaurantId);

    @Query("SELECT COUNT(o) FROM Orders o WHERE o.restaurant.id = :restaurantId AND o.orderDate >= :startOfDay AND o.orderDate < :endOfDay")
    Long countTodayOrdersByRestaurant(@Param("restaurantId") UUID restaurantId,
                                       @Param("startOfDay") LocalDateTime startOfDay,
                                       @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT COALESCE(SUM(o.finalAmount), 0) FROM Orders o WHERE o.restaurant.id = :restaurantId AND o.paymentStatus = :paymentStatus")
    Double sumRevenueByRestaurantAndPaymentStatus(@Param("restaurantId") UUID restaurantId,
                                                   @Param("paymentStatus") PaymentStatus paymentStatus);

    @Query("SELECT COALESCE(SUM(o.finalAmount), 0) FROM Orders o WHERE o.restaurant.id = :restaurantId AND o.paymentStatus = :paymentStatus AND o.orderDate >= :startOfDay AND o.orderDate < :endOfDay")
    Double sumTodayRevenueByRestaurant(@Param("restaurantId") UUID restaurantId,
                                        @Param("paymentStatus") PaymentStatus paymentStatus,
                                        @Param("startOfDay") LocalDateTime startOfDay,
                                        @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT COUNT(o) FROM Orders o WHERE o.restaurant.id = :restaurantId AND o.status IN :statuses")
    Long countActiveOrdersByRestaurant(@Param("restaurantId") UUID restaurantId,
                                        @Param("statuses") List<OrderStatus> statuses);

    // Admin queries
    @Query("SELECT COALESCE(SUM(o.finalAmount), 0) FROM Orders o WHERE o.paymentStatus = :paymentStatus")
    Double sumTotalRevenue(@Param("paymentStatus") PaymentStatus paymentStatus);

    @Query("SELECT COALESCE(SUM(o.finalAmount), 0) FROM Orders o WHERE o.paymentStatus = :paymentStatus AND o.orderDate >= :startOfDay AND o.orderDate < :endOfDay")
    Double sumTodayRevenue(@Param("paymentStatus") PaymentStatus paymentStatus,
                            @Param("startOfDay") LocalDateTime startOfDay,
                            @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT COUNT(o) FROM Orders o WHERE o.orderDate >= :startOfDay AND o.orderDate < :endOfDay")
    Long countTodayOrders(@Param("startOfDay") LocalDateTime startOfDay,
                           @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT COUNT(o) FROM Orders o WHERE o.status IN :statuses")
    Long countActiveOrders(@Param("statuses") List<OrderStatus> statuses);

    Long countByStatus(OrderStatus status);
}