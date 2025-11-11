package com.garv.foodApp.foodApp.Controller;

import com.garv.foodApp.foodApp.DTO.DeliveryDTO;
import com.garv.foodApp.foodApp.Service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<DeliveryDTO> createDelivery(@RequestBody DeliveryDTO deliveryDTO) {
        DeliveryDTO createdDelivery = deliveryService.createDelivery(deliveryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDelivery);
    }

    @PatchMapping("/{deliveryId}/status")
    public ResponseEntity<DeliveryDTO> updateDeliveryStatus(
            @PathVariable Long deliveryId,
            @RequestParam String status) {
        DeliveryDTO updatedDelivery = deliveryService.updateDeliveryStatus(deliveryId, status);
        return ResponseEntity.ok(updatedDelivery);
    }

    @PatchMapping("/{deliveryId}/assign/{deliveryPersonId}")
    public ResponseEntity<DeliveryDTO> assignDeliveryPerson(
            @PathVariable Long deliveryId,
            @PathVariable Long deliveryPersonId) {
        DeliveryDTO updatedDelivery = deliveryService.assignDeliveryPerson(deliveryId, deliveryPersonId);
        return ResponseEntity.ok(updatedDelivery);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<DeliveryDTO> getDeliveryByOrderId(@PathVariable Long orderId) {
        DeliveryDTO delivery = deliveryService.getDeliveryByOrderId(orderId);
        return ResponseEntity.ok(delivery);
    }

    @GetMapping("/delivery-person/{deliveryPersonId}")
    public ResponseEntity<List<DeliveryDTO>> getDeliveriesByDeliveryPerson(@PathVariable Long deliveryPersonId) {
        List<DeliveryDTO> deliveries = deliveryService.getDeliveriesByDeliveryPerson(deliveryPersonId);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping
    public ResponseEntity<List<DeliveryDTO>> getAllDeliveries() {
        List<DeliveryDTO> deliveries = deliveryService.getAllDeliveries();
        return ResponseEntity.ok(deliveries);
    }
}
