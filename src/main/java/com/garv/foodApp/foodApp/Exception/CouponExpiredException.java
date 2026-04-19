package com.garv.foodApp.foodApp.Exception;

public class CouponExpiredException extends RuntimeException {
    public CouponExpiredException(String code) {
        super("Coupon '" + code + "' has expired");
    }
}
