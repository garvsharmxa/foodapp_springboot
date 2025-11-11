package com.garv.foodApp.foodApp.Service;

import com.garv.foodApp.foodApp.DTO.DeliveryPersonDTO;
import com.garv.foodApp.foodApp.Entity.DeliveryPerson;
import com.garv.foodApp.foodApp.Repository.DeliveryPersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryPersonService {

    private final DeliveryPersonRepository deliveryPersonRepository;

    @Transactional
    public DeliveryPersonDTO createDeliveryPerson(DeliveryPersonDTO dto) {
        DeliveryPerson deliveryPerson = DeliveryPerson.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .vehicleType(dto.getVehicleType())
                .vehicleNumber(dto.getVehicleNumber())
                .isAvailable(true)
                .rating(5.0)
                .totalDeliveries(0)
                .build();
        
        DeliveryPerson saved = deliveryPersonRepository.save(deliveryPerson);
        return convertToDTO(saved);
    }

    @Transactional
    public DeliveryPersonDTO updateDeliveryPerson(Long id, DeliveryPersonDTO dto) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery person not found"));
        
        deliveryPerson.setName(dto.getName());
        deliveryPerson.setEmail(dto.getEmail());
        deliveryPerson.setPhone(dto.getPhone());
        deliveryPerson.setVehicleType(dto.getVehicleType());
        deliveryPerson.setVehicleNumber(dto.getVehicleNumber());
        deliveryPerson.setIsAvailable(dto.getIsAvailable());
        
        DeliveryPerson updated = deliveryPersonRepository.save(deliveryPerson);
        return convertToDTO(updated);
    }

    @Transactional
    public void updateAvailability(Long id, Boolean isAvailable) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery person not found"));
        deliveryPerson.setIsAvailable(isAvailable);
        deliveryPersonRepository.save(deliveryPerson);
    }

    public List<DeliveryPersonDTO> getAllDeliveryPersons() {
        return deliveryPersonRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<DeliveryPersonDTO> getAvailableDeliveryPersons() {
        return deliveryPersonRepository.findByIsAvailable(true).stream()
                .map(this::convertToDTO)
                .toList();
    }

    public DeliveryPersonDTO getDeliveryPersonById(Long id) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery person not found"));
        return convertToDTO(deliveryPerson);
    }

    @Transactional
    public void deleteDeliveryPerson(Long id) {
        if (!deliveryPersonRepository.existsById(id)) {
            throw new RuntimeException("Delivery person not found");
        }
        deliveryPersonRepository.deleteById(id);
    }

    private DeliveryPersonDTO convertToDTO(DeliveryPerson deliveryPerson) {
        return DeliveryPersonDTO.builder()
                .id(deliveryPerson.getId())
                .name(deliveryPerson.getName())
                .email(deliveryPerson.getEmail())
                .phone(deliveryPerson.getPhone())
                .vehicleType(deliveryPerson.getVehicleType())
                .vehicleNumber(deliveryPerson.getVehicleNumber())
                .isAvailable(deliveryPerson.getIsAvailable())
                .rating(deliveryPerson.getRating())
                .totalDeliveries(deliveryPerson.getTotalDeliveries())
                .build();
    }
}
