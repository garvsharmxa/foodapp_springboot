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
            if (userRepository.findByUsername("admin").isEmpty()) {
                Users admin = new Users();
                admin.setUsername("admin");
                admin.setEmail("admin@foodapp.com");
                admin.setPassword(passwordEncoder.encode("admin1234"));
                admin.setRole(Users.Role.ADMIN);
                admin.setEnabled(true);
                userRepository.save(admin);
                System.out.println("✅ Default Admin User created");
            } else {
                System.out.println("ℹ️ Admin user already exists");
            }

            if (userRepository.findByEmail("garvsharmxa@gmail.com").isEmpty()) {
                Users garv = new Users();
                garv.setUsername("garvsharmxa");
                garv.setEmail("garvsharmxa@gmail.com");
                garv.setPassword(passwordEncoder.encode("admin@123"));
                garv.setRole(Users.Role.ADMIN);
                garv.setEnabled(true);
                userRepository.save(garv);
                System.out.println("✅ Hardcoded Admin User garv created");
            }
        };
    }
}
