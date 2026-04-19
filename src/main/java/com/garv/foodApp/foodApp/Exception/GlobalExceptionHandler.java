package com.garv.foodApp.foodApp.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, Object>> buildErrorResponse(String error, String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("status", status.value());
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRestaurantNotFound(RestaurantNotFoundException ex) {
        return buildErrorResponse("Restaurant Not Found", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCustomerNotFound(CustomerNotFoundException ex) {
        return buildErrorResponse("Customer Not Found", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FoodItemsNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleFoodItemsNotFound(FoodItemsNotFoundException ex) {
        return buildErrorResponse("Food Item Not Found", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrdersNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleOrdersNotFound(OrdersNotFoundException ex) {
        return buildErrorResponse("Order Not Found", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {
        return buildErrorResponse("User Not Found", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CouponNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCouponNotFound(CouponNotFoundException ex) {
        return buildErrorResponse("Coupon Not Found", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CouponExpiredException.class)
    public ResponseEntity<Map<String, Object>> handleCouponExpired(CouponExpiredException ex) {
        return buildErrorResponse("Coupon Expired", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CouponInvalidException.class)
    public ResponseEntity<Map<String, Object>> handleCouponInvalid(CouponInvalidException ex) {
        return buildErrorResponse("Invalid Coupon", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MinimumOrderValueException.class)
    public ResponseEntity<Map<String, Object>> handleMinimumOrderValue(MinimumOrderValueException ex) {
        return buildErrorResponse("Minimum Order Value Not Met", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CartRestaurantMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleCartRestaurantMismatch(CartRestaurantMismatchException ex) {
        return buildErrorResponse("Cart Restaurant Mismatch", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrderCancellationException.class)
    public ResponseEntity<Map<String, Object>> handleOrderCancellation(OrderCancellationException ex) {
        return buildErrorResponse("Order Cancellation Failed", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RestaurantClosedException.class)
    public ResponseEntity<Map<String, Object>> handleRestaurantClosed(RestaurantClosedException ex) {
        return buildErrorResponse("Restaurant Closed", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return buildErrorResponse("Access Denied", "You do not have permission to access this resource.", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", "Validation Error");
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("timestamp", LocalDateTime.now().toString());

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage()));
        errorResponse.put("fieldErrors", fieldErrors);
        errorResponse.put("message", "Request validation failed. Check fieldErrors for details.");

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        return buildErrorResponse("Error", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return buildErrorResponse("Internal Server Error", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
