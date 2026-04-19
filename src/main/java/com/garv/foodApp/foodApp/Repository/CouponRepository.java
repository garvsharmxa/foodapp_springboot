package com.garv.foodApp.foodApp.Repository;

import com.garv.foodApp.foodApp.Entity.Coupon;
import com.garv.foodApp.foodApp.Entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CouponRepository extends JpaRepository<Coupon, UUID> {

    Optional<Coupon> findByCode(String code);

    @Query("SELECT c FROM Coupon c WHERE c.isActive = true AND c.validFrom <= :now AND c.validTill >= :now")
    List<Coupon> findActiveCoupons(LocalDateTime now);

    List<Coupon> findByRestaurant(Restaurant restaurant);

    List<Coupon> findByRestaurantIsNull(); // Global coupons

    @Query("SELECT c FROM Coupon c WHERE c.code = :code AND c.isActive = true AND c.validFrom <= :now AND c.validTill >= :now")
    Optional<Coupon> findActiveByCode(String code, LocalDateTime now);
}
