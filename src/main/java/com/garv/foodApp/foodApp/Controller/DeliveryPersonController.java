package com.garv.foodApp.foodApp.Controller;

import com.garv.foodApp.foodApp.DTO.DeliveryPersonDTO;
import com.garv.foodApp.foodApp.Service.DeliveryPersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/delivery-persons")
@RequiredArgsConstructor
public class DeliveryPersonController {

    private final DeliveryPersonService deliveryPersonService;

    @PostMapping
    public ResponseEntity<DeliveryPersonDTO> createDeliveryPerson(@RequestBody DeliveryPersonDTO dto) {
        DeliveryPersonDTO created = deliveryPersonService.createDeliveryPerson(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryPersonDTO> updateDeliveryPerson(
            @PathVariable Long id,
            @RequestBody DeliveryPersonDTO dto) {
        DeliveryPersonDTO updated = deliveryPersonService.updateDeliveryPerson(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<Void> updateAvailability(
            @PathVariable Long id,
            @RequestParam Boolean isAvailable) {
        deliveryPersonService.updateAvailability(id, isAvailable);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<DeliveryPersonDTO>> getAllDeliveryPersons() {
        List<DeliveryPersonDTO> deliveryPersons = deliveryPersonService.getAllDeliveryPersons();
        return ResponseEntity.ok(deliveryPersons);
    }

    @GetMapping("/available")
    public ResponseEntity<List<DeliveryPersonDTO>> getAvailableDeliveryPersons() {
        List<DeliveryPersonDTO> deliveryPersons = deliveryPersonService.getAvailableDeliveryPersons();
        return ResponseEntity.ok(deliveryPersons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryPersonDTO> getDeliveryPersonById(@PathVariable Long id) {
        DeliveryPersonDTO deliveryPerson = deliveryPersonService.getDeliveryPersonById(id);
        return ResponseEntity.ok(deliveryPerson);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeliveryPerson(@PathVariable Long id) {
        deliveryPersonService.deleteDeliveryPerson(id);
        return ResponseEntity.noContent().build();
    }
}
