package com.garv.foodApp.foodApp.Service;

import com.garv.foodApp.foodApp.DTO.CouponDTO;
import com.garv.foodApp.foodApp.Entity.Coupon;
import com.garv.foodApp.foodApp.Entity.Restaurant;
import com.garv.foodApp.foodApp.Exception.CouponExpiredException;
import com.garv.foodApp.foodApp.Exception.CouponInvalidException;
import com.garv.foodApp.foodApp.Exception.CouponNotFoundException;
import com.garv.foodApp.foodApp.Exception.MinimumOrderValueException;
import com.garv.foodApp.foodApp.Repository.CouponRepository;
import com.garv.foodApp.foodApp.Repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public Coupon createCoupon(CouponDTO couponDTO) {
        Coupon coupon = Coupon.builder()
                .code(couponDTO.getCode().toUpperCase())
                .description(couponDTO.getDescription())
                .discountType(couponDTO.getDiscountType())
                .discountValue(couponDTO.getDiscountValue())
                .minimumOrderValue(couponDTO.getMinimumOrderValue())
                .maxDiscountAmount(couponDTO.getMaxDiscountAmount())
                .validFrom(couponDTO.getValidFrom())
                .validTill(couponDTO.getValidTill())
                .usageLimit(couponDTO.getUsageLimit())
                .isActive(couponDTO.getIsActive() != null ? couponDTO.getIsActive() : true)
                .build();

        // Set restaurant if restaurant-specific
        if (couponDTO.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findById(couponDTO.getRestaurantId())
                    .orElseThrow(() -> new RuntimeException("Restaurant not found: " + couponDTO.getRestaurantId()));
            coupon.setRestaurant(restaurant);
        }

        return couponRepository.save(coupon);
    }

    @Transactional
    public Coupon updateCoupon(UUID id, CouponDTO couponDTO) {
        Coupon coupon = getCouponById(id);

        if (couponDTO.getCode() != null) {
            coupon.setCode(couponDTO.getCode().toUpperCase());
        }
        if (couponDTO.getDescription() != null) {
            coupon.setDescription(couponDTO.getDescription());
        }
        if (couponDTO.getDiscountType() != null) {
            coupon.setDiscountType(couponDTO.getDiscountType());
        }
        if (couponDTO.getDiscountValue() != null) {
            coupon.setDiscountValue(couponDTO.getDiscountValue());
        }
        if (couponDTO.getMinimumOrderValue() != null) {
            coupon.setMinimumOrderValue(couponDTO.getMinimumOrderValue());
        }
        if (couponDTO.getMaxDiscountAmount() != null) {
            coupon.setMaxDiscountAmount(couponDTO.getMaxDiscountAmount());
        }
        if (couponDTO.getValidFrom() != null) {
            coupon.setValidFrom(couponDTO.getValidFrom());
        }
        if (couponDTO.getValidTill() != null) {
            coupon.setValidTill(couponDTO.getValidTill());
        }
        if (couponDTO.getUsageLimit() != null) {
            coupon.setUsageLimit(couponDTO.getUsageLimit());
        }
        if (couponDTO.getIsActive() != null) {
            coupon.setIsActive(couponDTO.getIsActive());
        }

        return couponRepository.save(coupon);
    }

    public Coupon getCouponById(UUID id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException(id));
    }

    public Coupon getCouponByCode(String code) {
        return couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new CouponNotFoundException(code));
    }

    public List<CouponDTO> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CouponDTO> getActiveCoupons() {
        return couponRepository.findActiveCoupons(LocalDateTime.now()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteCoupon(UUID id) {
        if (!couponRepository.existsById(id)) {
            throw new CouponNotFoundException(id);
        }
        couponRepository.deleteById(id);
    }

    /**
     * Validate coupon for use
     */
    public Coupon validateCoupon(String code, Double orderAmount, UUID restaurantId) {
        Coupon coupon = couponRepository.findActiveByCode(code.toUpperCase(), LocalDateTime.now())
                .orElseThrow(() -> new CouponNotFoundException(code));

        // Check if coupon is valid
        if (!coupon.isValid()) {
            if (LocalDateTime.now().isAfter(coupon.getValidTill())) {
                throw new CouponExpiredException(code);
            }
            if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
                throw new CouponInvalidException(code, "Usage limit reached");
            }
            throw new CouponInvalidException(code, "Coupon is not active");
        }

        // Check restaurant-specific coupon
        if (coupon.getRestaurant() != null && !coupon.getRestaurant().getId().equals(restaurantId)) {
            throw new CouponInvalidException(code, "This coupon is valid only for " + coupon.getRestaurant().getName());
        }

        // Check minimum order value
        if (!coupon.meetsMinimumOrderValue(orderAmount)) {
            throw new MinimumOrderValueException(coupon.getMinimumOrderValue(), orderAmount);
        }

        return coupon;
    }

    /**
     * Increment coupon usage count
     */
    @Transactional
    public void incrementUsageCount(Coupon coupon) {
        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);
    }

    /**
     * Convert Coupon entity to DTO
     */
    public CouponDTO convertToCouponDTO(Coupon coupon) {
        return convertToDTO(coupon);
    }

    private CouponDTO convertToDTO(Coupon coupon) {
        return CouponDTO.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .description(coupon.getDescription())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .minimumOrderValue(coupon.getMinimumOrderValue())
                .maxDiscountAmount(coupon.getMaxDiscountAmount())
                .validFrom(coupon.getValidFrom())
                .validTill(coupon.getValidTill())
                .usageLimit(coupon.getUsageLimit())
                .usedCount(coupon.getUsedCount())
                .isActive(coupon.getIsActive())
                .restaurantId(coupon.getRestaurant() != null ? coupon.getRestaurant().getId() : null)
                .restaurantName(coupon.getRestaurant() != null ? coupon.getRestaurant().getName() : null)
                .build();
    }
}
