package com.garv.foodApp.foodApp.DTO;

import com.garv.foodApp.foodApp.Entity.OrderStatus;
import com.garv.foodApp.foodApp.Entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponseDTO {
    private UUID id;
    private OrderStatus status;
    private PaymentStatus paymentStatus;

    // User info
    private UUID userId;
    private String username;
    private String userEmail;

    // Restaurant info
    private UUID restaurantId;
    private String restaurantName;
    private String restaurantImageUrl;

    // Items
    private List<OrderItemDTO> items;

    // Amounts
    private Double totalAmount;
    private Double discountAmount;
    private Double deliveryFee;
    private Double finalAmount;

    // Coupon
    private String couponCode;
    private Double couponDiscount;

    // Razorpay
    private String razorpayOrderId;
    private String razorpayPaymentId;

    // Timestamps
    private LocalDateTime orderDate;
    private LocalDateTime confirmedAt;
    private LocalDateTime preparingAt;
    private LocalDateTime readyAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    private String cancellationReason;

    private String specialInstructions;
}
