package com.garv.foodApp.foodApp.Service;

import com.garv.foodApp.foodApp.Entity.FoodItems;
import com.garv.foodApp.foodApp.Exception.FoodItemsNotFoundException;
import com.garv.foodApp.foodApp.Repository.FoodItemsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FoodItemService {

    private final FoodItemsRepository foodItemsRepository;

    public List<FoodItems> getAllFoodItems() {
        return foodItemsRepository.findAll();
    }

    public FoodItems createFoodItem(FoodItems foodItem) {
        return foodItemsRepository.save(foodItem);
    }

    public FoodItems updateFoodItem(UUID id, FoodItems foodItemDetails) {
        FoodItems foodItem = foodItemsRepository.findById(id)
                .orElseThrow(() -> new FoodItemsNotFoundException(id));
        foodItem.setName(foodItemDetails.getName());
        foodItem.setPrice(foodItemDetails.getPrice());
        foodItem.setDescription(foodItemDetails.getDescription());
        foodItem.setImageUrl(foodItemDetails.getImageUrl());
        return foodItemsRepository.save(foodItem);
    }

    public void deleteFoodItem(UUID id) {
        foodItemsRepository.deleteById(id);
    }

    public FoodItems getFoodItemById(UUID id) {
        return foodItemsRepository.findById(id)
                .orElseThrow(() -> new FoodItemsNotFoundException(id));
    }
}
