package com.garv.foodApp.foodApp.Service;

import com.garv.foodApp.foodApp.DTO.ReviewDTO;
import com.garv.foodApp.foodApp.Entity.Customer;
import com.garv.foodApp.foodApp.Entity.Orders;
import com.garv.foodApp.foodApp.Entity.Restaurant;
import com.garv.foodApp.foodApp.Entity.Review;
import com.garv.foodApp.foodApp.Repository.CustomerRepository;
import com.garv.foodApp.foodApp.Repository.OrdersRepository;
import com.garv.foodApp.foodApp.Repository.RestaurantRepository;
import com.garv.foodApp.foodApp.Repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrdersRepository ordersRepository;

    @Transactional
    public ReviewDTO createReview(ReviewDTO reviewDTO) {
        Customer customer = customerRepository.findById(reviewDTO.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        Restaurant restaurant = restaurantRepository.findById(reviewDTO.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        
        Orders order = null;
        if (reviewDTO.getOrderId() != null) {
            order = ordersRepository.findById(reviewDTO.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found"));
        }
        
        Review review = Review.builder()
                .customer(customer)
                .restaurant(restaurant)
                .order(order)
                .rating(reviewDTO.getRating())
                .comment(reviewDTO.getComment())
                .createdAt(LocalDateTime.now())
                .build();
        
        Review savedReview = reviewRepository.save(review);
        
        // Update restaurant rating
        updateRestaurantRating(restaurant.getId());
        
        return convertToDTO(savedReview);
    }

    @Transactional
    public ReviewDTO updateReview(Long reviewId, ReviewDTO reviewDTO) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        review.setUpdatedAt(LocalDateTime.now());
        
        Review updatedReview = reviewRepository.save(review);
        
        // Update restaurant rating
        updateRestaurantRating(review.getRestaurant().getId());
        
        return convertToDTO(updatedReview);
    }

    public List<ReviewDTO> getReviewsByRestaurant(Long restaurantId) {
        return reviewRepository.findByRestaurantId(restaurantId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<ReviewDTO> getReviewsByCustomer(Long customerId) {
        return reviewRepository.findByCustomerId(customerId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    public ReviewDTO getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        return convertToDTO(review);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        Long restaurantId = review.getRestaurant().getId();
        reviewRepository.delete(review);
        
        // Update restaurant rating after deletion
        updateRestaurantRating(restaurantId);
    }

    private void updateRestaurantRating(Long restaurantId) {
        Double averageRating = reviewRepository.findAverageRatingByRestaurantId(restaurantId);
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        restaurant.setRating(averageRating != null ? averageRating : 0.0);
        restaurantRepository.save(restaurant);
    }

    private ReviewDTO convertToDTO(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .customerId(review.getCustomer() != null ? review.getCustomer().getId() : null)
                .customerName(review.getCustomer() != null ? review.getCustomer().getName() : null)
                .restaurantId(review.getRestaurant() != null ? review.getRestaurant().getId() : null)
                .restaurantName(review.getRestaurant() != null ? review.getRestaurant().getName() : null)
                .orderId(review.getOrder() != null ? review.getOrder().getId() : null)
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
