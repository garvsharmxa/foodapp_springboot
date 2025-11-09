package com.garv.foodApp.foodApp.Service;

import com.garv.foodApp.foodApp.DTO.RestaurantDTO;
import com.garv.foodApp.foodApp.Entity.Restaurant;
import com.garv.foodApp.foodApp.Exception.RestaurantNotFoundException;
import com.garv.foodApp.foodApp.Repository.RestaurantRepository;
import com.garv.foodApp.foodApp.mapper.RestaurantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;

    /**
     * GET all restaurants.
     * Fetches all Restaurant entities from DB and maps them to DTOs using MapStruct.
     */
    public List<RestaurantDTO> getAllRestaurants() {
        return restaurantRepository.findAll()
                .stream()
                .map(restaurantMapper::toRestaurantDTO)
                .toList();
    }

    /**
     * CREATE a new restaurant.
     * Converts DTO → Entity using MapStruct, saves it to DB, then converts Entity → DTO before returning.
     */
    public RestaurantDTO createRestaurant(RestaurantDTO restaurantDTO) {
        Restaurant restaurant = restaurantMapper.toRestaurant(restaurantDTO); // DTO → Entity
        Restaurant saved = restaurantRepository.save(restaurant);             // Save to DB
        return restaurantMapper.toRestaurantDTO(saved);                        // Entity → DTO
    }

    /**
     * GET restaurant by ID.
     * Fetches a restaurant from DB. Throws RestaurantNotFoundException if not found.
     */
    public RestaurantDTO getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));
        return restaurantMapper.toRestaurantDTO(restaurant); // Convert to DTO before returning
    }

    /**
     * UPDATE restaurant by ID.
     * Fetches existing restaurant entity, updates its fields from DTO using MapStruct,
     * saves updated entity to DB, and returns the updated DTO.
     */
    public RestaurantDTO updateRestaurant(Long id, RestaurantDTO restaurantDTO) {
        // Fetch existing restaurant or throw exception if not found
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));

        // Map updated fields from DTO → Entity
        restaurantMapper.updateRestaurantFromDTO(restaurantDTO, restaurant);

        // Save updated entity and convert to DTO
        Restaurant updated = restaurantRepository.save(restaurant);
        return restaurantMapper.toRestaurantDTO(updated);
    }
}
