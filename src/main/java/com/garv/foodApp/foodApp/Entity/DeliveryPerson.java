package com.garv.foodApp.foodApp.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryPerson {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String phone;
    
    private String vehicleType; // BIKE, SCOOTER, CAR, BICYCLE
    
    private String vehicleNumber;
    
    private Boolean isAvailable;
    
    private Double rating;
    
    private Integer totalDeliveries;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private Users user;
    
    @OneToMany(mappedBy = "deliveryPerson", cascade = CascadeType.ALL)
    private List<Delivery> deliveries;
}
