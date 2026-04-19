package com.garv.foodApp.foodApp.Exception;

import java.util.UUID;

public class CouponNotFoundException extends RuntimeException {
    public CouponNotFoundException(UUID id) {
        super("Coupon not found with id: " + id);
    }

    public CouponNotFoundException(String code) {
        super("Coupon not found with code: " + code);
    }
}
