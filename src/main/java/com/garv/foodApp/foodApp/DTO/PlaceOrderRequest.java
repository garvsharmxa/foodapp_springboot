package com.garv.foodApp.foodApp.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceOrderRequest {
    @NotNull(message = "Restaurant ID is required")
    private UUID restaurantId;

    @NotEmpty(message = "Order items cannot be empty")
    private List<PlaceOrderItemRequest> items;

    private String couponCode;
    private String specialInstructions;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PlaceOrderItemRequest {
        @NotNull(message = "Food Item ID is required")
        private UUID foodItemId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
    }
}
