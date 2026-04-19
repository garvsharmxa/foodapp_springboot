package com.garv.foodApp.foodApp.Config;

import com.garv.foodApp.foodApp.Entity.*;
import com.garv.foodApp.foodApp.Repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(
            LocationRepository locationRepository,
            RestaurantRepository restaurantRepository,
            FoodItemsRepository foodItemsRepository,
            CouponRepository couponRepository,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository) {
        return args -> {
            // Check if data already exists
            if (locationRepository.count() > 0) {
                System.out.println("Database already seeded. Skipping...");
                return;
            }

            System.out.println("Seeding database with sample data...");

            // ═══════════════════════════════════════
            // 1. CREATE LOCATIONS
            // ═══════════════════════════════════════
            Location iitDelhi = new Location();
            iitDelhi.setName("IIT Delhi");
            iitDelhi.setAddress("Hauz Khas, New Delhi, Delhi 110016");
            locationRepository.save(iitDelhi);

            Location delhiUniversity = new Location();
            delhiUniversity.setName("Delhi University");
            delhiUniversity.setAddress("North Campus, Delhi 110007");
            locationRepository.save(delhiUniversity);

            Location connaught = new Location();
            connaught.setName("Connaught Place");
            connaught.setAddress("Connaught Place, New Delhi, Delhi 110001");
            locationRepository.save(connaught);

            System.out.println("✓ Created 3 locations");

            // ═══════════════════════════════════════
            // 2. CREATE RESTAURANTS
            // ═══════════════════════════════════════

            // Restaurant 1: Domino's Pizza
            Restaurant dominos = new Restaurant();
            dominos.setName("Domino's Pizza");
            dominos.setAddress("IIT Delhi Gate 1, Hauz Khas");
            dominos.setCategory("Pizza");
            dominos.setRating(4.5);
            dominos.setIsOpen(true);
            dominos.setLocation(iitDelhi);
            dominos.setImageUrl("https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400");
            restaurantRepository.save(dominos);

            // Restaurant 2: McDonald's
            Restaurant mcdonalds = new Restaurant();
            mcdonalds.setName("McDonald's");
            mcdonalds.setAddress("IIT Delhi Main Gate");
            mcdonalds.setCategory("Burger");
            mcdonalds.setRating(4.3);
            mcdonalds.setIsOpen(true);
            mcdonalds.setLocation(iitDelhi);
            mcdonalds.setImageUrl("https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400");
            restaurantRepository.save(mcdonalds);

            // Restaurant 3: Subway
            Restaurant subway = new Restaurant();
            subway.setName("Subway");
            subway.setAddress("Near SBI, IIT Delhi");
            subway.setCategory("Sandwich");
            subway.setRating(4.2);
            subway.setIsOpen(true);
            subway.setLocation(iitDelhi);
            subway.setImageUrl("https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=400");
            restaurantRepository.save(subway);

            // Restaurant 4: KFC
            Restaurant kfc = new Restaurant();
            kfc.setName("KFC");
            kfc.setAddress("Delhi University North Campus");
            kfc.setCategory("Chicken");
            kfc.setRating(4.4);
            kfc.setIsOpen(true);
            kfc.setLocation(delhiUniversity);
            kfc.setImageUrl("https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=400");
            restaurantRepository.save(kfc);

            // Restaurant 5: Burger King
            Restaurant burgerKing = new Restaurant();
            burgerKing.setName("Burger King");
            burgerKing.setAddress("Connaught Place Inner Circle");
            burgerKing.setCategory("Burger");
            burgerKing.setRating(4.1);
            burgerKing.setIsOpen(true);
            burgerKing.setLocation(connaught);
            burgerKing.setImageUrl("https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=400");
            restaurantRepository.save(burgerKing);

            // Restaurant 6: Starbucks
            Restaurant starbucks = new Restaurant();
            starbucks.setName("Starbucks");
            starbucks.setAddress("Connaught Place");
            starbucks.setCategory("Cafe");
            starbucks.setRating(4.6);
            starbucks.setIsOpen(true);
            starbucks.setLocation(connaught);
            starbucks.setImageUrl("https://images.unsplash.com/photo-1559496417-e7f25cb247f6?w=400");
            restaurantRepository.save(starbucks);

            System.out.println("✓ Created 6 restaurants");

            // ═══════════════════════════════════════
            // 3. CREATE FOOD ITEMS
            // ═══════════════════════════════════════

            // Domino's Menu
            createFoodItem("Margherita Pizza", "Classic cheese pizza", 299.0, dominos,
                    "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=400", foodItemsRepository);
            createFoodItem("Pepperoni Pizza", "Spicy pepperoni with cheese", 399.0, dominos,
                    "https://images.unsplash.com/photo-1628840042765-356cda07504e?w=400", foodItemsRepository);
            createFoodItem("Veggie Supreme", "Loaded with vegetables", 349.0, dominos,
                    "https://images.unsplash.com/photo-1571997478779-2adcbbe9ab2f?w=400", foodItemsRepository);
            createFoodItem("Garlic Breadsticks", "Cheesy garlic breadsticks", 149.0, dominos,
                    "https://images.unsplash.com/photo-1573140401552-3fab0b24306f?w=400", foodItemsRepository);

            // McDonald's Menu
            createFoodItem("Big Mac", "Iconic double-decker burger", 250.0, mcdonalds,
                    "https://images.unsplash.com/photo-1550547660-d9450f859349?w=400", foodItemsRepository);
            createFoodItem("McChicken Burger", "Crispy chicken burger", 180.0, mcdonalds,
                    "https://images.unsplash.com/photo-1606755962773-d324e0a13086?w=400", foodItemsRepository);
            createFoodItem("French Fries", "Crispy golden fries", 99.0, mcdonalds,
                    "https://images.unsplash.com/photo-1576107232684-1279f390859f?w=400", foodItemsRepository);
            createFoodItem("McFlurry", "Soft serve with toppings", 120.0, mcdonalds,
                    "https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=400", foodItemsRepository);

            // Subway Menu
            createFoodItem("Veggie Delite Sub", "Fresh vegetables in sub", 199.0, subway,
                    "https://images.unsplash.com/photo-1509722747041-616f39b57569?w=400", foodItemsRepository);
            createFoodItem("Chicken Teriyaki", "Grilled chicken with teriyaki", 249.0, subway,
                    "https://images.unsplash.com/photo-1520072959219-c595dc870360?w=400", foodItemsRepository);
            createFoodItem("Tuna Sub", "Fresh tuna sandwich", 229.0, subway,
                    "https://images.unsplash.com/photo-1553909489-cd47e0907980?w=400", foodItemsRepository);

            // KFC Menu
            createFoodItem("Fried Chicken Bucket", "8 pieces of crispy chicken", 599.0, kfc,
                    "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=400", foodItemsRepository);
            createFoodItem("Zinger Burger", "Spicy chicken burger", 199.0, kfc,
                    "https://images.unsplash.com/photo-1606755962773-d324e0a13086?w=400", foodItemsRepository);
            createFoodItem("Popcorn Chicken", "Bite-sized chicken pieces", 149.0, kfc,
                    "https://images.unsplash.com/photo-1562967914-608f82629710?w=400", foodItemsRepository);

            // Burger King Menu
            createFoodItem("Whopper", "Flame-grilled beef burger", 220.0, burgerKing,
                    "https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=400", foodItemsRepository);
            createFoodItem("Chicken Royale", "Crispy chicken burger", 200.0, burgerKing,
                    "https://images.unsplash.com/photo-1606755962773-d324e0a13086?w=400", foodItemsRepository);
            createFoodItem("Onion Rings", "Crispy onion rings", 89.0, burgerKing,
                    "https://images.unsplash.com/photo-1639024471283-03518883512d?w=400", foodItemsRepository);

            // Starbucks Menu
            createFoodItem("Caffe Latte", "Espresso with steamed milk", 250.0, starbucks,
                    "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=400", foodItemsRepository);
            createFoodItem("Cappuccino", "Espresso with foam", 230.0, starbucks,
                    "https://images.unsplash.com/photo-1572442388796-11668a67e53d?w=400", foodItemsRepository);
            createFoodItem("Chocolate Muffin", "Rich chocolate muffin", 180.0, starbucks,
                    "https://images.unsplash.com/photo-1607958996333-41aef7caefaa?w=400", foodItemsRepository);

            System.out.println("✓ Created 25+ food items");

            // ═══════════════════════════════════════
            // 4. CREATE COUPONS
            // ═══════════════════════════════════════

            // Percentage discount coupon
            Coupon save20 = new Coupon();
            save20.setCode("SAVE20");
            save20.setDescription("Get 20% off on orders above ₹500");
            save20.setDiscountType(DiscountType.PERCENTAGE);
            save20.setDiscountValue(20.0);
            save20.setMinimumOrderValue(500.0);
            save20.setMaxDiscountAmount(200.0);
            save20.setValidFrom(LocalDateTime.now().minusDays(1));
            save20.setValidTill(LocalDateTime.now().plusMonths(3));
            save20.setIsActive(true);
            save20.setUsageLimit(1000);
            couponRepository.save(save20);

            // Fixed amount coupon
            Coupon first50 = new Coupon();
            first50.setCode("FIRST50");
            first50.setDescription("₹50 off on first order");
            first50.setDiscountType(DiscountType.FIXED_AMOUNT);
            first50.setDiscountValue(50.0);
            first50.setMinimumOrderValue(200.0);
            first50.setValidFrom(LocalDateTime.now().minusDays(1));
            first50.setValidTill(LocalDateTime.now().plusMonths(6));
            first50.setIsActive(true);
            first50.setUsageLimit(500);
            couponRepository.save(first50);

            // Restaurant-specific coupon
            Coupon pizza100 = new Coupon();
            pizza100.setCode("PIZZA100");
            pizza100.setDescription("₹100 off on Domino's orders");
            pizza100.setDiscountType(DiscountType.FIXED_AMOUNT);
            pizza100.setDiscountValue(100.0);
            pizza100.setMinimumOrderValue(400.0);
            pizza100.setValidFrom(LocalDateTime.now().minusDays(1));
            pizza100.setValidTill(LocalDateTime.now().plusMonths(1));
            pizza100.setIsActive(true);
            pizza100.setRestaurant(dominos);
            couponRepository.save(pizza100);

            System.out.println("✓ Created 3 coupons");

            // ═══════════════════════════════════════
            // 5. CREATE TEST USER
            // ═══════════════════════════════════════

            Users testUser = new Users();
            testUser.setUsername("test_user");
            testUser.setEmail("test@example.com");
            testUser.setPassword(passwordEncoder.encode("password123"));
            testUser.setRole(Users.Role.USER);
            userRepository.save(testUser);

            System.out.println("✓ Created test user (email: test@example.com, password: password123)");

            System.out.println("\n═══════════════════════════════════════");
            System.out.println("✅ DATABASE SEEDING COMPLETED!");
            System.out.println("═══════════════════════════════════════");
            System.out.println("Locations: " + locationRepository.count());
            System.out.println("Restaurants: " + restaurantRepository.count());
            System.out.println("Food Items: " + foodItemsRepository.count());
            System.out.println("Coupons: " + couponRepository.count());
            System.out.println("Users: " + userRepository.count());
            System.out.println("═══════════════════════════════════════\n");
        };
    }

    private void createFoodItem(String name, String description, Double price,
            Restaurant restaurant, String imageUrl,
            FoodItemsRepository repository) {
        FoodItems item = new FoodItems();
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price.intValue()); // FoodItems uses int price
        item.setRestaurant(restaurant);
        item.setImageUrl(imageUrl);
        repository.save(item);
    }
}
