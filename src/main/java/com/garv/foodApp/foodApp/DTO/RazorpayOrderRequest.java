package com.garv.foodApp.foodApp.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RazorpayOrderRequest {
    private Long orderId;
    private Double amount;
    private String currency;
    private String receipt;
}
