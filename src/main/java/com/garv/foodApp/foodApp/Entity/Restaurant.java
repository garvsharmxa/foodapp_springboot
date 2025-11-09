package com.garv.foodApp.foodApp.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    private String phone;

    private Double rating;  // Changed from String

    private String ownerName;

    private Double deliveryRadius;  // Changed from String

    private Double minimumOrderValue; // Changed from String

    private String imageUrl;

    @Column(unique = true)
    private String email;

    @Column(name = "is_open")
    private Boolean isOpen;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "restaurant")
    @JsonManagedReference
    private List<FoodItems> menu;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "restaurant-orders")
    private List<Orders> orders;
}
