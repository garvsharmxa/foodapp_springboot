package com.garv.foodApp.foodApp.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String address;

    private String phone;

    private Double rating;

    private String ownerName;

    private Double deliveryRadius;

    private Double minimumOrderValue;

    private String imageUrl;

    @Column(unique = true)
    private String email;

    @Column(name = "is_open")
    private Boolean isOpen;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "restaurant")
    @JsonManagedReference
    private List<FoodItems> foodItems;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"restaurant", "user", "foodItems", "orderItems", "statusHistory", "coupon"})
    private List<Orders> orders;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id")
    @JsonIgnoreProperties({"password", "otp", "otpGeneratedTime", "authorities",
            "accountNonExpired", "credentialsNonExpired", "accountNonLocked", "enabled"})
    private Users ownerUser;
}
