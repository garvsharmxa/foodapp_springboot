package com.garv.foodApp.foodApp.DTO;

import com.garv.foodApp.foodApp.Entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateOrderStatusRequest {

    private UUID orderId;
    private OrderStatus status;
    private String remarks;
}
