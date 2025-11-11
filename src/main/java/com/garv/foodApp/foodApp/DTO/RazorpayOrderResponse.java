package com.garv.foodApp.foodApp.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RazorpayOrderResponse {
    private String razorpayOrderId;
    private Double amount;
    private String currency;
    private String receipt;
    private String status;
    private Long orderId;
    private String keyId;
}
