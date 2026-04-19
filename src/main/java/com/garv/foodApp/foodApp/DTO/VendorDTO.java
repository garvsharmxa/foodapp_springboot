package com.garv.foodApp.foodApp.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorDTO {
    private UUID id;
    private String name;
    private String address;
    private String phone;
    private Double rating;
    private String imageUrl;
    private Boolean isOpen;
    private String category;
    private UUID locationId;
}
