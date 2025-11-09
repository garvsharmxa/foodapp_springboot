package com.garv.foodApp.foodApp.Repository;

import com.garv.foodApp.foodApp.Entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}