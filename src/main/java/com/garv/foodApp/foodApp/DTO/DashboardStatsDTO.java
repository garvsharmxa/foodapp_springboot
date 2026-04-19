package com.garv.foodApp.foodApp.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardStatsDTO {
    private Long totalOrders;
    private Long todayOrders;
    private Long activeOrders;
    private Long completedOrders;
    private Long cancelledOrders;
    private Double totalRevenue;
    private Double todayRevenue;
    private Long totalUsers;
    private Long totalRestaurants;
    private Long totalFoodItems;
}
