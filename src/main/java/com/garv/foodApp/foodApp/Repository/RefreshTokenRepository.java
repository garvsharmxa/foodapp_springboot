package com.garv.foodApp.foodApp.Repository;

import com.garv.foodApp.foodApp.Entity.RefreshToken;
import com.garv.foodApp.foodApp.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    Optional<RefreshToken> findByToken(String token);
    
    void deleteByUser(Users user);
    
    Optional<RefreshToken> findByUser(Users user);
}
