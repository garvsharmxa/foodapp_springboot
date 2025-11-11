package com.garv.foodApp.foodApp.Repository;

import com.garv.foodApp.foodApp.Entity.DeliveryPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryPersonRepository extends JpaRepository<DeliveryPerson, Long> {
    Optional<DeliveryPerson> findByEmail(String email);
    List<DeliveryPerson> findByIsAvailable(Boolean isAvailable);
    Optional<DeliveryPerson> findByUserId(Long userId);
}
