package com.garv.foodApp.foodApp.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Coupon Entity
 * Represents discount coupons that can be applied to orders
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String code; // Coupon code (e.g., "SAVE20", "FIRST50")

    @Column(nullable = false)
    private String description; // Description of the offer

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType; // PERCENTAGE or FIXED_AMOUNT

    @Column(nullable = false)
    private Double discountValue; // 20 for 20% or 50 for ₹50

    private Double minimumOrderValue; // Minimum order amount to apply coupon

    private Double maxDiscountAmount; // Maximum discount for percentage coupons

    @Column(nullable = false)
    private LocalDateTime validFrom; // Coupon valid from date

    @Column(nullable = false)
    private LocalDateTime validTill; // Coupon expiry date

    private Integer usageLimit; // Total number of times coupon can be used (null = unlimited)

    @Column(nullable = false)
    @Builder.Default
    private Integer usedCount = 0; // Number of times coupon has been used

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true; // Is coupon active

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant; // Null for global coupons, specific restaurant for restaurant-specific

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * Check if coupon is valid for use
     */
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return isActive
                && now.isAfter(validFrom)
                && now.isBefore(validTill)
                && (usageLimit == null || usedCount < usageLimit);
    }

    /**
     * Check if minimum order value is met
     */
    public boolean meetsMinimumOrderValue(Double orderAmount) {
        return minimumOrderValue == null || orderAmount >= minimumOrderValue;
    }

    /**
     * Calculate discount amount for given order total
     */
    public Double calculateDiscount(Double orderAmount) {
        if (!meetsMinimumOrderValue(orderAmount)) {
            return 0.0;
        }

        double discount = 0.0;
        if (discountType == DiscountType.PERCENTAGE) {
            discount = (orderAmount * discountValue) / 100.0;
            if (maxDiscountAmount != null && discount > maxDiscountAmount) {
                discount = maxDiscountAmount;
            }
        } else {
            discount = discountValue;
        }

        // Discount cannot exceed order amount
        return Math.min(discount, orderAmount);
    }
}
