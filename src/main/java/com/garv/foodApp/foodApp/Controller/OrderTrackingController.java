package com.garv.foodApp.foodApp.Controller;

import com.garv.foodApp.foodApp.DTO.OrderTrackingDTO;
import com.garv.foodApp.foodApp.DTO.UpdateOrderStatusRequest;
import com.garv.foodApp.foodApp.Service.OrderTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class OrderTrackingController {

    private final OrderTrackingService orderTrackingService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * WebSocket: Merchant updates order status
     * Client sends to: /app/order/update-status
     * Broadcasting handled by OrderTrackingService via SimpMessagingTemplate
     * Customer subscribes to: /topic/order/{orderId}
     * Merchant subscribes to: /topic/restaurant/{restaurantId}/orders
     */
    @MessageMapping("/order/update-status")
    public void updateOrderStatusWs(@Payload UpdateOrderStatusRequest request) {
        orderTrackingService.updateOrderStatus(
                request.getOrderId(),
                request.getStatus(),
                "Merchant",
                request.getRemarks());
    }

    /**
     * WebSocket: Client subscribes and gets current order status
     * Client sends to: /app/order/subscribe/{orderId}
     * Response sent to: /topic/order/{orderId}
     */
    @MessageMapping("/order/subscribe/{orderId}")
    public void subscribeToOrder(@DestinationVariable UUID orderId) {
        OrderTrackingDTO current = OrderTrackingDTO.builder()
                .orderId(orderId)
                .statusMessage("Connected to order tracking")
                .build();
        messagingTemplate.convertAndSend("/topic/order/" + orderId, current);
    }
}
