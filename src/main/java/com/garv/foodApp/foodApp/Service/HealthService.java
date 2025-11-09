package com.garv.foodApp.foodApp.Service;
import com.garv.foodApp.foodApp.Entity.Health;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

    public Health getHealthStatus() {
        // You can add more checks like DB connection, cache status, etc.
        String message = "FoodApp API is running smoothly. All systems operational.";
        return new Health("UP", message);
    }
}
