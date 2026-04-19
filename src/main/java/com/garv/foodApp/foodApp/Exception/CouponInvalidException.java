package com.garv.foodApp.foodApp.Exception;

public class CouponInvalidException extends RuntimeException {
    public CouponInvalidException(String message) {
        super(message);
    }

    public CouponInvalidException(String code, String reason) {
        super("Coupon '" + code + "' is invalid: " + reason);
    }
}
