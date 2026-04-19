package com.garv.foodApp.foodApp.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemDTO {
    private UUID id;
    private UUID foodItemId;
    private String foodItemName;
    private Integer quantity;
    private Double price;
    private Double totalPrice; // price * quantity
}
