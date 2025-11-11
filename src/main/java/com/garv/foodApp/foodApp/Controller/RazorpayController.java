package com.garv.foodApp.foodApp.Controller;

import com.garv.foodApp.foodApp.DTO.PaymentDTO;
import com.garv.foodApp.foodApp.DTO.RazorpayOrderRequest;
import com.garv.foodApp.foodApp.DTO.RazorpayOrderResponse;
import com.garv.foodApp.foodApp.DTO.RazorpayPaymentVerification;
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
     * POST /api/v1/razorpay/create-order
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
     * POST /api/v1/razorpay/verify-payment
     */
    @PostMapping("/verify-payment")
    public ResponseEntity<PaymentDTO> verifyPayment(@RequestBody RazorpayPaymentVerification verification) {
        try {
            PaymentDTO payment = razorpayService.verifyPayment(verification);
            return ResponseEntity.ok(payment);
        } catch (RazorpayException e) {
            throw new RuntimeException("Payment verification failed: " + e.getMessage());
        }
    }

    /**
     * Handle failed payment
     * POST /api/v1/razorpay/payment-failed
     */
    @PostMapping("/payment-failed")
    public ResponseEntity<PaymentDTO> handleFailedPayment(
            @RequestParam String razorpayOrderId,
            @RequestParam(required = false) String reason) {
        PaymentDTO payment = razorpayService.handleFailedPayment(razorpayOrderId, reason);
        return ResponseEntity.ok(payment);
    }

    /**
     * Get payment by Razorpay order ID
     * GET /api/v1/razorpay/payment/{razorpayOrderId}
     */
    @GetMapping("/payment/{razorpayOrderId}")
    public ResponseEntity<PaymentDTO> getPaymentByRazorpayOrderId(@PathVariable String razorpayOrderId) {
        PaymentDTO payment = razorpayService.getPaymentByRazorpayOrderId(razorpayOrderId);
        return ResponseEntity.ok(payment);
    }
}
