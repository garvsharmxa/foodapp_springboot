package com.garv.foodApp.foodApp.DTO;

import com.garv.foodApp.foodApp.Entity.DiscountType;
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
public class CouponDTO {

    private UUID id;
    private String code;
    private String description;
    private DiscountType discountType;
    private Double discountValue;
    private Double minimumOrderValue;
    private Double maxDiscountAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validTill;
    private Integer usageLimit;
    private Integer usedCount;
    private Boolean isActive;
    private UUID restaurantId; // null for global coupons
    private String restaurantName;
}
