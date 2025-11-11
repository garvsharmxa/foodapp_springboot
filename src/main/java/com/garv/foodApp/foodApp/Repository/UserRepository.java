package com.garv.foodApp.foodApp.Repository;

import com.garv.foodApp.foodApp.Entity.Users;   // <-- use your entity
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByUserName(String userName);  // match entity field name
    
    Optional<Users> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    boolean existsByUserName(String userName);
}
