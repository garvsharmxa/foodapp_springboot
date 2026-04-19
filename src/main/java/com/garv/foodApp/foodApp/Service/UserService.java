package com.garv.foodApp.foodApp.Service;

import com.garv.foodApp.foodApp.DTO.UserProfileDTO;
import com.garv.foodApp.foodApp.Entity.Location;
import com.garv.foodApp.foodApp.Entity.Users;
import com.garv.foodApp.foodApp.Repository.LocationRepository;
import com.garv.foodApp.foodApp.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    public Users getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public Users getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    /**
     * Get the currently authenticated user
     */
    public Users getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return getUserByUsername(username);
    }

    /**
     * Get current user's profile as DTO
     */
    public UserProfileDTO getCurrentUserProfile() {
        Users user = getCurrentUser();
        return UserProfileDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .locationId(user.getLocation() != null ? user.getLocation().getId() : null)
                .locationName(user.getLocation() != null ? user.getLocation().getName() : null)
                .build();
    }

    /**
     * Convert Users entity to UserProfileDTO
     */
    public UserProfileDTO toProfileDTO(Users user) {
        return UserProfileDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .locationId(user.getLocation() != null ? user.getLocation().getId() : null)
                .locationName(user.getLocation() != null ? user.getLocation().getName() : null)
                .build();
    }

    /**
     * Update user profile (username)
     */
    public UserProfileDTO updateProfile(String newUsername, UUID locationId) {
        Users user = getCurrentUser();
        if (newUsername != null && !newUsername.isBlank()) {
            // Check uniqueness
            if (userRepository.existsByUsername(newUsername) && !user.getUsername().equals(newUsername)) {
                throw new RuntimeException("Username '" + newUsername + "' is already taken.");
            }
            user.setUsername(newUsername);
        }

        if (locationId != null) {
            Location location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new RuntimeException("Location not found"));
            user.setLocation(location);
        }

        Users saved = userRepository.save(user);
        return toProfileDTO(saved);
    }
}
