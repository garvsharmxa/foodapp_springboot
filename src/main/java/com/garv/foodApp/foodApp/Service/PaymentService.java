package com.garv.foodApp.foodApp.Service;

import com.garv.foodApp.foodApp.DTO.PaymentDTO;
import com.garv.foodApp.foodApp.Entity.Orders;
import com.garv.foodApp.foodApp.Entity.Payment;
import com.garv.foodApp.foodApp.Repository.OrdersRepository;
import com.garv.foodApp.foodApp.Repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrdersRepository ordersRepository;

    @Transactional
    public PaymentDTO initiatePayment(PaymentDTO paymentDTO) {
        Orders order = ordersRepository.findById(paymentDTO.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + paymentDTO.getOrderId()));
        
        Payment payment = Payment.builder()
                .order(order)
                .amount(paymentDTO.getAmount())
                .paymentMethod(paymentDTO.getPaymentMethod())
                .paymentStatus("PENDING")
                .paymentGateway(paymentDTO.getPaymentGateway())
                .transactionId(generateTransactionId())
                .paymentDate(LocalDateTime.now())
                .build();
        
        Payment savedPayment = paymentRepository.save(payment);
        return convertToDTO(savedPayment);
    }

    @Transactional
    public PaymentDTO confirmPayment(Long paymentId, String status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
        
        payment.setPaymentStatus(status);
        payment.setPaymentDate(LocalDateTime.now());
        
        Payment updatedPayment = paymentRepository.save(payment);
        return convertToDTO(updatedPayment);
    }

    public PaymentDTO getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order id: " + orderId));
        return convertToDTO(payment);
    }

    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        return convertToDTO(payment);
    }

    private String generateTransactionId() {
        return "TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private PaymentDTO convertToDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .orderId(payment.getOrder() != null ? payment.getOrder().getId() : null)
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .transactionId(payment.getTransactionId())
                .paymentDate(payment.getPaymentDate())
                .paymentGateway(payment.getPaymentGateway())
                .paymentDetails(payment.getPaymentDetails())
                .build();
    }
}
