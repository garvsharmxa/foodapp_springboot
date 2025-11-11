package com.garv.foodApp.foodApp.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Orders order;
    
    @Column(nullable = false)
    private Double amount;
    
    @Column(nullable = false)
    private String paymentMethod; // CREDIT_CARD, DEBIT_CARD, UPI, CASH_ON_DELIVERY, WALLET
    
    @Column(nullable = false)
    private String paymentStatus; // PENDING, COMPLETED, FAILED, REFUNDED
    
    private String transactionId;
    
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;
    
    private String paymentGateway; // RAZORPAY, STRIPE, PAYTM, etc.
    
    @Column(length = 500)
    private String paymentDetails; // Additional payment information as JSON
}
