package com.garv.foodApp.foodApp.Service;

import com.garv.foodApp.foodApp.DTO.DeliveryDTO;
import com.garv.foodApp.foodApp.Entity.Delivery;
import com.garv.foodApp.foodApp.Entity.DeliveryPerson;
import com.garv.foodApp.foodApp.Entity.Orders;
import com.garv.foodApp.foodApp.Repository.DeliveryPersonRepository;
import com.garv.foodApp.foodApp.Repository.DeliveryRepository;
import com.garv.foodApp.foodApp.Repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrdersRepository ordersRepository;
    private final DeliveryPersonRepository deliveryPersonRepository;

    @Transactional
    public DeliveryDTO createDelivery(DeliveryDTO deliveryDTO) {
        Orders order = ordersRepository.findById(deliveryDTO.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + deliveryDTO.getOrderId()));
        
        Delivery delivery = Delivery.builder()
                .order(order)
                .deliveryStatus("ASSIGNED")
                .deliveryAddress(deliveryDTO.getDeliveryAddress())
                .deliveryInstructions(deliveryDTO.getDeliveryInstructions())
                .deliveryFee(deliveryDTO.getDeliveryFee())
                .estimatedDeliveryTime(deliveryDTO.getEstimatedDeliveryTime())
                .assignedAt(LocalDateTime.now())
                .build();
        
        if (deliveryDTO.getDeliveryPersonId() != null) {
            DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(deliveryDTO.getDeliveryPersonId())
                    .orElseThrow(() -> new RuntimeException("Delivery person not found"));
            delivery.setDeliveryPerson(deliveryPerson);
        }
        
        Delivery savedDelivery = deliveryRepository.save(delivery);
        return convertToDTO(savedDelivery);
    }

    @Transactional
    public DeliveryDTO updateDeliveryStatus(Long deliveryId, String status) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found with id: " + deliveryId));
        
        delivery.setDeliveryStatus(status);
        
        if ("PICKED_UP".equals(status)) {
            delivery.setPickedUpAt(LocalDateTime.now());
        } else if ("DELIVERED".equals(status)) {
            delivery.setDeliveredAt(LocalDateTime.now());
        }
        
        Delivery updatedDelivery = deliveryRepository.save(delivery);
        return convertToDTO(updatedDelivery);
    }

    @Transactional
    public DeliveryDTO assignDeliveryPerson(Long deliveryId, Long deliveryPersonId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found with id: " + deliveryId));
        
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(deliveryPersonId)
                .orElseThrow(() -> new RuntimeException("Delivery person not found with id: " + deliveryPersonId));
        
        delivery.setDeliveryPerson(deliveryPerson);
        delivery.setAssignedAt(LocalDateTime.now());
        
        Delivery updatedDelivery = deliveryRepository.save(delivery);
        return convertToDTO(updatedDelivery);
    }

    public DeliveryDTO getDeliveryByOrderId(Long orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery not found for order id: " + orderId));
        return convertToDTO(delivery);
    }

    public List<DeliveryDTO> getDeliveriesByDeliveryPerson(Long deliveryPersonId) {
        return deliveryRepository.findByDeliveryPersonId(deliveryPersonId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<DeliveryDTO> getAllDeliveries() {
        return deliveryRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    private DeliveryDTO convertToDTO(Delivery delivery) {
        return DeliveryDTO.builder()
                .id(delivery.getId())
                .orderId(delivery.getOrder() != null ? delivery.getOrder().getId() : null)
                .deliveryPersonId(delivery.getDeliveryPerson() != null ? delivery.getDeliveryPerson().getId() : null)
                .deliveryPersonName(delivery.getDeliveryPerson() != null ? delivery.getDeliveryPerson().getName() : null)
                .deliveryStatus(delivery.getDeliveryStatus())
                .assignedAt(delivery.getAssignedAt())
                .pickedUpAt(delivery.getPickedUpAt())
                .deliveredAt(delivery.getDeliveredAt())
                .deliveryAddress(delivery.getDeliveryAddress())
                .deliveryInstructions(delivery.getDeliveryInstructions())
                .deliveryFee(delivery.getDeliveryFee())
                .estimatedDeliveryTime(delivery.getEstimatedDeliveryTime())
                .trackingUrl(delivery.getTrackingUrl())
                .build();
    }
}
