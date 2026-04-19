package com.garv.foodApp.foodApp.Controller;

import com.garv.foodApp.foodApp.DTO.RazorpayOrderRequest;
import com.garv.foodApp.foodApp.DTO.RazorpayOrderResponse;
import com.garv.foodApp.foodApp.DTO.RazorpayPaymentVerification;
import com.garv.foodApp.foodApp.Entity.Orders;
import com.garv.foodApp.foodApp.Service.RazorpayService;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/razorpay")
@RequiredArgsConstructor
public class RazorpayController {

    private final RazorpayService razorpayService;

    /**
     * Create Razorpay order
     */
    @PostMapping("/create-order")
    public ResponseEntity<RazorpayOrderResponse> createOrder(@RequestBody RazorpayOrderRequest request) {
        try {
            RazorpayOrderResponse response = razorpayService.createRazorpayOrder(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RazorpayException e) {
            throw new RuntimeException("Failed to create Razorpay order: " + e.getMessage());
        }
    }

    /**
     * Verify Razorpay payment
     */
    @PostMapping("/verify-payment")
    public ResponseEntity<Orders> verifyPayment(@RequestBody RazorpayPaymentVerification verification) {
        try {
            Orders order = razorpayService.verifyPayment(verification);
            return ResponseEntity.ok(order);
        } catch (RazorpayException e) {
            throw new RuntimeException("Payment verification failed: " + e.getMessage());
        }
    }

    /**
     * Handle failed payment
     */
    @PostMapping("/payment-failed")
    public ResponseEntity<Orders> handleFailedPayment(
            @RequestParam String razorpayOrderId,
            @RequestParam(required = false) String reason) {
        Orders order = razorpayService.handleFailedPayment(razorpayOrderId, reason);
        return ResponseEntity.ok(order);
    }

    /**
     * Get order by Razorpay order ID
     */
    @GetMapping("/order/{razorpayOrderId}")
    public ResponseEntity<Orders> getOrderByRazorpayOrderId(@PathVariable String razorpayOrderId) {
        Orders order = razorpayService.getOrderByRazorpayOrderId(razorpayOrderId);
        return ResponseEntity.ok(order);
    }
}
