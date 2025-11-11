package com.garv.foodApp.foodApp.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status; // PENDING, CONFIRMED, PREPARING, READY, OUT_FOR_DELIVERY, DELIVERED, CANCELLED

    @ManyToOne(fetch = FetchType.LAZY) // Changed to LAZY
    @JoinColumn(name = "customer_id")
    @JsonIgnore // Add this to prevent JSON serialization issues
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY) // Changed to LAZY
    @JoinColumn(name = "restaurant_id")
    @JsonIgnore // Add this to prevent JSON serialization issues
    private Restaurant restaurant;

    @ManyToMany(fetch = FetchType.LAZY) // Changed to LAZY
    @JoinTable(
            name = "order_fooditem",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "food_id")
    )
    @JsonIgnore // Add this to prevent JSON serialization issues
    private List<FoodItems> foodItems;
    
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;
    
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Delivery delivery;
    
    private Double totalAmount;
    
    private LocalDateTime orderDate;
    
    private String deliveryAddress;
    
    private String specialInstructions;
}