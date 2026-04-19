package com.garv.foodApp.foodApp.Controller;

import com.garv.foodApp.foodApp.DTO.CouponDTO;
import com.garv.foodApp.foodApp.Entity.Coupon;
import com.garv.foodApp.foodApp.Service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * User-facing coupon endpoints.
 * Admin coupon CRUD is handled in AdminController.
 */
@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    /**
     * Get active coupons (User accessible)
     * GET /user/coupons/active
     */
    @GetMapping("/user/coupons/active")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CouponDTO>> getActiveCoupons() {
        List<CouponDTO> coupons = couponService.getActiveCoupons();
        return ResponseEntity.ok(coupons);
    }

    /**
     * Validate coupon (User accessible)
     * GET /user/coupons/validate/{code}
     * Query params: ?amount=xxx&restaurantId=xxx
     */
    @GetMapping("/user/coupons/validate/{code}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ValidationResponse> validateCoupon(
            @PathVariable String code,
            @RequestParam Double amount,
            @RequestParam UUID restaurantId) {
        try {
            Coupon coupon = couponService.validateCoupon(code, amount, restaurantId);
            Double discount = coupon.calculateDiscount(amount);

            return ResponseEntity.ok(new ValidationResponse(
                    true,
                    "Coupon is valid",
                    discount,
                    amount - discount));
        } catch (Exception e) {
            return ResponseEntity.ok(new ValidationResponse(
                    false,
                    e.getMessage(),
                    0.0,
                    amount));
        }
    }

    /**
     * Inner class for coupon validation response
     */
    private static class ValidationResponse {
        public boolean isValid;
        public String message;
        public Double discountAmount;
        public Double finalAmount;

        public ValidationResponse(boolean isValid, String message, Double discountAmount, Double finalAmount) {
            this.isValid = isValid;
            this.message = message;
            this.discountAmount = discountAmount;
            this.finalAmount = finalAmount;
        }
    }
}
