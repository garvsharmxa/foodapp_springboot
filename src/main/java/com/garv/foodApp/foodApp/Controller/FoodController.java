package com.garv.foodApp.foodApp.Controller;

import com.garv.foodApp.foodApp.DTO.FoodItemsDTO;
import com.garv.foodApp.foodApp.Entity.FoodItems;
import com.garv.foodApp.foodApp.Exception.RestaurantNotFoundException;
import com.garv.foodApp.foodApp.Repository.FoodItemsRepository;
import com.garv.foodApp.foodApp.Repository.RestaurantRepository;
import com.garv.foodApp.foodApp.Service.FoodItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/foodItems")
@RequiredArgsConstructor
public class FoodController {

    private final FoodItemsRepository foodItemsRepository;
    private final RestaurantRepository restaurantRepository;
    private final FoodItemService foodItemService;

    @GetMapping
    public List<FoodItemsDTO> getAllFoodItems() {  // Fixed: Added proper generic type

        return foodItemService.getAllFoodItems();
    }

    @PostMapping
    public FoodItemsDTO addFoodItem(@RequestBody FoodItemsDTO foodItemsDTO) {
        return foodItemService.addFoodItem(foodItemsDTO);
    }

    @GetMapping("/{id}")  // Fixed: Added @PathVariable
    public FoodItemsDTO getFoodItemById(@PathVariable Long id) {
        return foodItemService.getFoodItemById(id);
    }



    @GetMapping("/restaurant/{restaurantId}")  // Fixed: Better path and @PathVariable
    public List<FoodItems> getFoodItemsByRestaurantId(@PathVariable Long restaurantId) {
        restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        return foodItemsRepository.findByRestaurantId(restaurantId);
    }
}