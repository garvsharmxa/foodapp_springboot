package com.garv.foodApp.foodApp.Config;
import com.garv.foodApp.foodApp.Service.CustomUserDetailService;
import com.garv.foodApp.foodApp.filters.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CSRF protection is disabled for this stateless REST API that uses JWT authentication.
        // JWT tokens are immune to CSRF attacks as they are not automatically sent by browsers
        // like cookies. Each request must explicitly include the JWT token in the Authorization header.
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - Authentication
                        .requestMatchers("/authenticate", "/auth/**", "/health/**").permitAll()
                        
                        // Public endpoints - Restaurant and Food browsing (no auth required)
                        .requestMatchers("/restaurants/**", "/foodItems/**").permitAll()
                        
                        // Admin endpoints for food management
                        .requestMatchers("/food/**", "/delivery-persons/**").hasAnyRole("ADMIN", "RESTAURANT_OWNER")
                        
                        // Customer endpoints (require authentication)
                        .requestMatchers("/cart/**", "/reviews/**", "/orders/**").hasAnyRole("CUSTOMER", "ADMIN")
                        
                        // Delivery person endpoints
                        .requestMatchers("/deliveries/**").hasAnyRole("DELIVERY_PERSON", "ADMIN")
                        
                        // Payment endpoints (accessible by customers and admin)
                        .requestMatchers("/payments/**", "/razorpay/**").hasAnyRole("CUSTOMER", "ADMIN")
                        
                        // Customer management (admin only)
                        .requestMatchers("/customers/**").hasRole("ADMIN")
                        
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailService();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(daoAuthenticationProvider);
    }

}
