package com.garv.foodApp.foodApp.Service;

import com.garv.foodApp.foodApp.DTO.RestaurantDTO;
import com.garv.foodApp.foodApp.Entity.Restaurant;
import com.garv.foodApp.foodApp.Entity.FoodItems;
import com.garv.foodApp.foodApp.Entity.Users;
import com.garv.foodApp.foodApp.Exception.RestaurantNotFoundException;
import com.garv.foodApp.foodApp.Repository.RestaurantRepository;
import com.garv.foodApp.foodApp.mapper.RestaurantMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;

    public Page<RestaurantDTO> getAllRestaurants(Pageable pageable) {
        return restaurantRepository.findAll(pageable)
                .map(restaurantMapper::toRestaurantDTO);
    }

    public Page<RestaurantDTO> getRestaurantsByLocation(UUID locationId, Pageable pageable) {
        return restaurantRepository.findByLocationId(locationId, pageable)
                .map(restaurantMapper::toRestaurantDTO);
    }

    public List<FoodItems> getFoodItemsByRestaurantId(UUID restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        return restaurant.getFoodItems();
    }

    public RestaurantDTO createRestaurant(RestaurantDTO restaurantDTO) {
        Restaurant restaurant = restaurantMapper.toRestaurant(restaurantDTO);
        Restaurant saved = restaurantRepository.save(restaurant);
        return restaurantMapper.toRestaurantDTO(saved);
    }

    public RestaurantDTO getRestaurantById(UUID id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));
        return restaurantMapper.toRestaurantDTO(restaurant);
    }

    public RestaurantDTO updateRestaurant(UUID id, RestaurantDTO restaurantDTO) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));
        restaurantMapper.updateRestaurantFromDTO(restaurantDTO, restaurant);
        Restaurant updated = restaurantRepository.save(restaurant);
        return restaurantMapper.toRestaurantDTO(updated);
    }

    public void deleteRestaurant(UUID id) {
        if (!restaurantRepository.existsById(id)) {
            throw new RestaurantNotFoundException(id);
        }
        restaurantRepository.deleteById(id);
    }

    /**
     * Get restaurant owned by a specific user (merchant)
     */
    public RestaurantDTO getRestaurantByOwnerUserId(UUID ownerUserId) {
        Restaurant restaurant = restaurantRepository.findByOwnerUserId(ownerUserId)
                .orElseThrow(() -> new RuntimeException("No restaurant found for this merchant. Please contact admin."));
        return restaurantMapper.toRestaurantDTO(restaurant);
    }

    /**
     * Get restaurant entity by owner user ID
     */
    public Restaurant getRestaurantEntityByOwnerUserId(UUID ownerUserId) {
        return restaurantRepository.findByOwnerUserId(ownerUserId)
                .orElseThrow(() -> new RuntimeException("No restaurant found for this merchant."));
    }

    /**
     * Toggle restaurant open/close status
     */
    @Transactional
    public RestaurantDTO toggleRestaurantStatus(UUID restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        restaurant.setIsOpen(restaurant.getIsOpen() == null ? true : !restaurant.getIsOpen());
        Restaurant updated = restaurantRepository.save(restaurant);
        log.info("Restaurant {} toggled to {}", restaurantId, updated.getIsOpen() ? "OPEN" : "CLOSED");
        return restaurantMapper.toRestaurantDTO(updated);
    }

    /**
     * Search restaurants by name or category
     */
    public Page<RestaurantDTO> searchRestaurants(String query, Pageable pageable) {
        return restaurantRepository.searchByNameOrCategory(query, pageable)
                .map(restaurantMapper::toRestaurantDTO);
    }

    /**
     * Get open restaurants by location
     */
    public Page<RestaurantDTO> getOpenRestaurantsByLocation(UUID locationId, Pageable pageable) {
        return restaurantRepository.findByLocationIdAndIsOpenTrue(locationId, pageable)
                .map(restaurantMapper::toRestaurantDTO);
    }

    /**
     * Assign an owner user to a restaurant
     */
    @Transactional
    public RestaurantDTO assignOwner(UUID restaurantId, Users ownerUser) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        restaurant.setOwnerUser(ownerUser);
        Restaurant updated = restaurantRepository.save(restaurant);
        log.info("Restaurant {} assigned to user {}", restaurantId, ownerUser.getId());
        return restaurantMapper.toRestaurantDTO(updated);
    }
}
