package com.garv.foodApp.foodApp.Service;

import com.garv.foodApp.foodApp.Entity.Users;
import com.garv.foodApp.foodApp.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class AdminUserInitializer {

    @Bean
    public CommandLineRunner createAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUserName("admin").isEmpty()) {
                Users admin = new Users();
                admin.setUserName("admin");
                admin.setPassword(passwordEncoder.encode("admin1234"));
                admin.setRole("ROLE_ADMIN");
                userRepository.save(admin);
                System.out.println("✅ Default Admin User created");
            } else {
                System.out.println("ℹ️ Admin user already exists");
            }
        };
    }
}
