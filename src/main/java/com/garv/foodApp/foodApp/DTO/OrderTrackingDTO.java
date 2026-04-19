package com.garv.foodApp.foodApp.DTO;

import com.garv.foodApp.foodApp.Entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderTrackingDTO {

    private UUID orderId;
    private OrderStatus status;
    private OrderStatus previousStatus;
    private String statusMessage;
    private LocalDateTime timestamp;
    private String updatedBy;
    private String remarks;
    private LocalDateTime updatedAt;

    // Additional order info
    private UUID restaurantId;
    private String restaurantName;
    private Double totalAmount;
    private LocalDateTime estimatedReadyTime;
}
