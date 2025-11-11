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
public class Delivery {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Orders order;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_person_id")
    private DeliveryPerson deliveryPerson;
    
    @Column(nullable = false)
    private String deliveryStatus; // ASSIGNED, PICKED_UP, IN_TRANSIT, DELIVERED, CANCELLED
    
    private LocalDateTime assignedAt;
    
    private LocalDateTime pickedUpAt;
    
    private LocalDateTime deliveredAt;
    
    private String deliveryAddress;
    
    private String deliveryInstructions;
    
    private Double deliveryFee;
    
    private Integer estimatedDeliveryTime; // in minutes
    
    private String trackingUrl;
}
