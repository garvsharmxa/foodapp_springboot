package com.garv.foodApp.foodApp.Controller;

import com.garv.foodApp.foodApp.DTO.PaymentDTO;
import com.garv.foodApp.foodApp.Service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentDTO> initiatePayment(@RequestBody PaymentDTO paymentDTO) {
        PaymentDTO createdPayment = paymentService.initiatePayment(paymentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment);
    }

    @PatchMapping("/{paymentId}/confirm")
    public ResponseEntity<PaymentDTO> confirmPayment(
            @PathVariable Long paymentId,
            @RequestParam String status) {
        PaymentDTO updatedPayment = paymentService.confirmPayment(paymentId, status);
        return ResponseEntity.ok(updatedPayment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long id) {
        PaymentDTO payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentDTO> getPaymentByOrderId(@PathVariable Long orderId) {
        PaymentDTO payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(payment);
    }

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        List<PaymentDTO> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }
}
