package com.garv.foodApp.foodApp.Service;

import com.garv.foodApp.foodApp.DTO.FoodItemsDTO;
import com.garv.foodApp.foodApp.Entity.FoodItems;
import com.garv.foodApp.foodApp.Exception.FoodItemsNotFoundException;
import com.garv.foodApp.foodApp.Repository.FoodItemsRepository;
import com.garv.foodApp.foodApp.mapper.FoodItemMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // Marks this class as a Spring Service (business logic layer)
public class FoodItemService {

    // Repository to perform CRUD operations on FoodItems table
    private final FoodItemsRepository foodItemsRepository;

    // Mapper to convert between FoodItems entity and FoodItemsDTO
    private final FoodItemMapper foodItemMapper;

    /**
     * Constructor injection (recommended in Spring)
     * - Ensures immutability
     * - Makes unit testing easier
     */
    public FoodItemService(FoodItemsRepository foodItemsRepository,
                           FoodItemMapper foodItemMapper) {
        this.foodItemsRepository = foodItemsRepository;
        this.foodItemMapper = foodItemMapper;
    }

    /**
     * READ: Get all food items
     * - Fetches all records from DB
     * - Converts each FoodItems entity → FoodItemsDTO using MapStruct
     */
    public List<FoodItemsDTO> getAllFoodItems() {
        return foodItemsRepository.findAll()
                .stream()
                .map(foodItemMapper::toFoodItemDTO) // MapStruct handles mapping
                .toList();
    }

    /**
     * CREATE: Add a new food item
     * - Converts DTO → Entity
     * - Saves entity to DB
     * - Converts saved entity → DTO for API response
     * - Nested Restaurant mapping is handled automatically by MapStruct
     */
    public FoodItemsDTO addFoodItem(FoodItemsDTO foodItemsDTO) {
        FoodItems foodItems = foodItemMapper.toFoodItem(foodItemsDTO); // DTO → Entity
        FoodItems savedFoodItem = foodItemsRepository.save(foodItems); // Save to DB
        return foodItemMapper.toFoodItemDTO(savedFoodItem); // Entity → DTO
    }

    /**
     * READ: Get a food item by ID
     * - Fetches entity from DB
     * - Throws FoodItemsNotFoundException if not found
     * - Converts entity → DTO
     * - Nested Restaurant mapping handled automatically by MapStruct
     */
    public FoodItemsDTO getFoodItemById(Long id) {
        FoodItems foodItems = foodItemsRepository.findById(id)
                .orElseThrow(() -> new FoodItemsNotFoundException(id)); // Exception if not found

        return foodItemMapper.toFoodItemDTO(foodItems); // Entity → DTO
    }
}
