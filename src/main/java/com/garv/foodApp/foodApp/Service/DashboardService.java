package com.garv.foodApp.foodApp.Service;

import com.garv.foodApp.foodApp.DTO.DashboardStatsDTO;
import com.garv.foodApp.foodApp.Entity.OrderStatus;
import com.garv.foodApp.foodApp.Entity.PaymentStatus;
import com.garv.foodApp.foodApp.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrdersRepository ordersRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final FoodItemsRepository foodItemsRepository;

    private static final List<OrderStatus> ACTIVE_STATUSES = List.of(
            OrderStatus.PLACED, OrderStatus.CONFIRMED, OrderStatus.PREPARING, OrderStatus.READY);

    /**
     * Get admin dashboard statistics
     */
    public DashboardStatsDTO getAdminDashboardStats() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        return DashboardStatsDTO.builder()
                .totalOrders((long) ordersRepository.count())
                .todayOrders(ordersRepository.countTodayOrders(startOfDay, endOfDay))
                .activeOrders(ordersRepository.countActiveOrders(ACTIVE_STATUSES))
                .completedOrders(ordersRepository.countByStatus(OrderStatus.COMPLETED))
                .cancelledOrders(ordersRepository.countByStatus(OrderStatus.CANCELLED))
                .totalRevenue(ordersRepository.sumTotalRevenue(PaymentStatus.COMPLETED))
                .todayRevenue(ordersRepository.sumTodayRevenue(PaymentStatus.COMPLETED, startOfDay, endOfDay))
                .totalUsers((long) userRepository.count())
                .totalRestaurants((long) restaurantRepository.count())
                .totalFoodItems((long) foodItemsRepository.count())
                .build();
    }

    /**
     * Get merchant dashboard statistics for a specific restaurant
     */
    public DashboardStatsDTO getMerchantDashboardStats(UUID restaurantId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        return DashboardStatsDTO.builder()
                .totalOrders(ordersRepository.countByRestaurantId(restaurantId))
                .todayOrders(ordersRepository.countTodayOrdersByRestaurant(restaurantId, startOfDay, endOfDay))
                .activeOrders(ordersRepository.countActiveOrdersByRestaurant(restaurantId, ACTIVE_STATUSES))
                .totalRevenue(ordersRepository.sumRevenueByRestaurantAndPaymentStatus(restaurantId, PaymentStatus.COMPLETED))
                .todayRevenue(ordersRepository.sumTodayRevenueByRestaurant(restaurantId, PaymentStatus.COMPLETED, startOfDay, endOfDay))
                .build();
    }
}
