package com.garv.foodApp.foodApp.Service;

import com.garv.foodApp.foodApp.DTO.OrderResponseDTO;
import com.garv.foodApp.foodApp.DTO.OrderTrackingDTO;
import com.garv.foodApp.foodApp.Entity.OrderStatus;
import com.garv.foodApp.foodApp.Entity.OrderStatusHistory;
import com.garv.foodApp.foodApp.Entity.Orders;
import com.garv.foodApp.foodApp.Exception.OrdersNotFoundException;
import com.garv.foodApp.foodApp.Repository.OrderStatusHistoryRepository;
import com.garv.foodApp.foodApp.Repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderTrackingService {

    private final OrdersRepository ordersRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Update order status with real-time WebSocket notification
     */
    @Transactional
    public OrderTrackingDTO updateOrderStatus(UUID orderId, OrderStatus newStatus, String updatedBy, String remarks) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new OrdersNotFoundException(orderId));

        OrderStatus previousStatus = order.getStatus();

        // Update order status
        order.setStatus(newStatus);
        LocalDateTime now = LocalDateTime.now();

        switch (newStatus) {
            case CONFIRMED -> order.setConfirmedAt(now);
            case PREPARING -> order.setPreparingAt(now);
            case READY -> order.setReadyAt(now);
            case PICKED_UP -> order.setPickedUpAt(now);
            case COMPLETED -> {
                order.setCompletedAt(now);
            }
            case CANCELLED -> order.setCancelledAt(now);
            default -> {}
        }

        ordersRepository.save(order);

        // Save status history
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(newStatus);
        history.setUpdatedBy(updatedBy);
        history.setRemarks(remarks);
        statusHistoryRepository.save(history);

        // Build tracking DTO
        OrderTrackingDTO trackingDTO = OrderTrackingDTO.builder()
                .orderId(orderId)
                .status(newStatus)
                .previousStatus(previousStatus)
                .statusMessage(getStatusMessage(newStatus))
                .estimatedReadyTime(estimateReadyTime(order, newStatus))
                .updatedBy(updatedBy)
                .remarks(remarks)
                .updatedAt(now)
                .build();

        // Send WebSocket notification to customer (subscribing to /topic/order/{orderId})
        messagingTemplate.convertAndSend("/topic/order/" + orderId, trackingDTO);
        log.info("WebSocket notification sent for order {} -> {}", orderId, newStatus);

        // Send notification to merchant's restaurant topic
        if (order.getRestaurant() != null) {
            messagingTemplate.convertAndSend(
                    "/topic/restaurant/" + order.getRestaurant().getId() + "/orders",
                    trackingDTO);
        }

        return trackingDTO;
    }

    /**
     * Notify merchant of new order via WebSocket
     */
    public void notifyNewOrder(UUID restaurantId, OrderResponseDTO orderResponse) {
        messagingTemplate.convertAndSend(
                "/topic/restaurant/" + restaurantId + "/new-order",
                orderResponse);
        log.info("New order notification sent to restaurant {}", restaurantId);
    }

    /**
     * Get status message for display
     */
    private String getStatusMessage(OrderStatus status) {
        return switch (status) {
            case PLACED -> "Your order has been placed successfully!";
            case CONFIRMED -> "Your order has been confirmed by the restaurant.";
            case PREPARING -> "Your food is being prepared.";
            case READY -> "Your food is ready for pickup!";
            case PICKED_UP -> "Order has been picked up.";
            case COMPLETED -> "Order completed. Thank you!";
            case CANCELLED -> "Order has been cancelled.";
        };
    }

    /**
     * Estimate ready time based on current status
     */
    private LocalDateTime estimateReadyTime(Orders order, OrderStatus status) {
        LocalDateTime now = LocalDateTime.now();
        return switch (status) {
            case CONFIRMED -> now.plusMinutes(20);
            case PREPARING -> now.plusMinutes(15);
            case READY -> now;
            default -> null;
        };
    }

    /**
     * Get order status history
     */
    public List<OrderStatusHistory> getOrderStatusHistory(UUID orderId) {
        return statusHistoryRepository.findByOrderIdOrderByTimestampDesc(orderId);
    }
}
