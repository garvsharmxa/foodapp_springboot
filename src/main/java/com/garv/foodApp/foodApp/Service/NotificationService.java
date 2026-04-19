package com.garv.foodApp.foodApp.Service;

import com.garv.foodApp.foodApp.DTO.OrderResponseDTO;
import com.garv.foodApp.foodApp.DTO.OrderTrackingDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service for sending real-time notifications via WebSocket
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Notify customer about order status change
     * Customer subscribes to: /topic/order/{orderId}
     */
    public void notifyOrderStatusChange(UUID orderId, OrderTrackingDTO trackingDTO) {
        messagingTemplate.convertAndSend("/topic/order/" + orderId, trackingDTO);
        log.info("Order status notification sent for order: {} -> {}", orderId, trackingDTO.getStatus());
    }

    /**
     * Notify merchant about new order
     * Merchant subscribes to: /topic/restaurant/{restaurantId}/new-order
     */
    public void notifyNewOrder(UUID restaurantId, OrderResponseDTO orderResponse) {
        messagingTemplate.convertAndSend(
                "/topic/restaurant/" + restaurantId + "/new-order",
                orderResponse);
        log.info("New order notification sent to restaurant: {}", restaurantId);
    }

    /**
     * Notify merchant about order updates (e.g., customer cancellation)
     * Merchant subscribes to: /topic/restaurant/{restaurantId}/orders
     */
    public void notifyMerchantOrderUpdate(UUID restaurantId, OrderTrackingDTO trackingDTO) {
        messagingTemplate.convertAndSend(
                "/topic/restaurant/" + restaurantId + "/orders",
                trackingDTO);
        log.info("Order update notification sent to restaurant: {}", restaurantId);
    }

    /**
     * Send a generic notification to a specific user
     * User subscribes to: /user/queue/notifications
     */
    public void notifyUser(String username, Object payload) {
        messagingTemplate.convertAndSendToUser(username, "/queue/notifications", payload);
        log.info("Notification sent to user: {}", username);
    }

    /**
     * Broadcast admin notification
     * Admin subscribes to: /topic/admin/notifications
     */
    public void notifyAdmin(Object payload) {
        messagingTemplate.convertAndSend("/topic/admin/notifications", payload);
        log.info("Admin notification broadcast sent");
    }
}
