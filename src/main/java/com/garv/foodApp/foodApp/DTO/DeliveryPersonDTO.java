package com.garv.foodApp.foodApp.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryPersonDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String vehicleType;
    private String vehicleNumber;
    private Boolean isAvailable;
    private Double rating;
    private Integer totalDeliveries;
}
