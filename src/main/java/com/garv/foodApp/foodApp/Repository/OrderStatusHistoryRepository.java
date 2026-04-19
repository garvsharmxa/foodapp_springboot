package com.garv.foodApp.foodApp.Repository;

import com.garv.foodApp.foodApp.Entity.Orders;
import com.garv.foodApp.foodApp.Entity.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, UUID> {

    List<OrderStatusHistory> findByOrderOrderByTimestampDesc(Orders order);

    List<OrderStatusHistory> findByOrderIdOrderByTimestampDesc(UUID orderId);
}
