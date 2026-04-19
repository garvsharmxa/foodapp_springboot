package com.garv.foodApp.foodApp.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartSummaryDTO {

    private UUID cartId;
    private UUID userId;
    private UUID restaurantId;
    private String restaurantName;
    private List<CartItemDTO> items;
    private Integer itemCount;
    private Double totalAmount;
    private String couponCode;
    private Double discountAmount;
    private Double deliveryFee;
    private Double finalAmount;
}
