package com.garv.foodApp.foodApp.Repository;

import com.garv.foodApp.foodApp.Entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

    Page<Restaurant> findByLocationId(UUID locationId, Pageable pageable);

    Optional<Restaurant> findByOwnerUserId(UUID ownerUserId);

    @Query("SELECT r FROM Restaurant r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(r.category) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Restaurant> searchByNameOrCategory(@Param("query") String query, Pageable pageable);

    Page<Restaurant> findByIsOpenTrue(Pageable pageable);

    Page<Restaurant> findByLocationIdAndIsOpenTrue(UUID locationId, Pageable pageable);
}