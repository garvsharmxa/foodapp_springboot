package com.garv.foodApp.foodApp.Service;

import com.garv.foodApp.foodApp.DTO.*;
import com.garv.foodApp.foodApp.Entity.*;
import com.garv.foodApp.foodApp.Exception.FoodItemsNotFoundException;
import com.garv.foodApp.foodApp.Exception.OrderCancellationException;
import com.garv.foodApp.foodApp.Exception.OrdersNotFoundException;
import com.garv.foodApp.foodApp.Exception.RestaurantClosedException;
import com.garv.foodApp.foodApp.Exception.RestaurantNotFoundException;
import com.garv.foodApp.foodApp.Repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServices {

    private final OrdersRepository ordersRepository;
    private final FoodItemsRepository foodItemsRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    /**
     * Place order from cart for a user
     */
    @Transactional
    public OrderResponseDTO placeOrderFromCart(UUID userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart is empty"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Restaurant restaurant = cart.getRestaurant();
        if (restaurant == null) {
            throw new RuntimeException("No restaurant associated with cart");
        }

        if (restaurant.getIsOpen() != null && !restaurant.getIsOpen()) {
            throw new RestaurantClosedException(restaurant.getName());
        }

        if (user.getLocation() == null || restaurant.getLocation() == null || !user.getLocation().getId().equals(restaurant.getLocation().getId())) {
            throw new RuntimeException("You can only order from restaurants in your assigned location.");
        }

        // Create order
        Orders order = Orders.builder()
                .user(user)
                .restaurant(restaurant)
                .status(OrderStatus.PLACED)
                .paymentStatus(PaymentStatus.PENDING)
                .totalAmount(cart.getTotalAmount())
                .discountAmount(cart.getDiscountAmount())
                .deliveryFee(0.0)
                .finalAmount(cart.getFinalAmount())
                .coupon(cart.getCoupon())
                .build();
        order.calculateFinalAmount();

        Orders savedOrder = ordersRepository.save(order);

        // Create order items from cart items
        List<OrderItem> orderItems = new ArrayList<>();
        List<FoodItems> foodItemsList = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder)
                    .foodItem(cartItem.getFoodItem())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getPrice())
                    .itemName(cartItem.getFoodItem().getName())
                    .build();
            orderItems.add(orderItem);
            foodItemsList.add(cartItem.getFoodItem());
        }

        orderItemRepository.saveAll(orderItems);
        savedOrder.setOrderItems(orderItems);
        savedOrder.setFoodItems(foodItemsList);
        ordersRepository.save(savedOrder);

        // Increment coupon usage if applied
        if (cart.getCoupon() != null) {
            Coupon coupon = cart.getCoupon();
            coupon.setUsedCount(coupon.getUsedCount() + 1);
            couponRepository.save(coupon);
        }

        // Clear the cart
        cartItemRepository.deleteByCart(cart);
        cart.getItems().clear();
        cart.setCoupon(null);
        cart.setTotalAmount(0.0);
        cart.setDiscountAmount(0.0);
        cart.setFinalAmount(0.0);
        cart.setRestaurant(null);
        cartRepository.save(cart);

        log.info("Order placed successfully: {} for user: {}", savedOrder.getId(), userId);
        return toOrderResponseDTO(savedOrder);
    }

    /**
     * Place order directly (without cart)
     */
    @Transactional
    public OrderResponseDTO placeOrder(UUID userId, PlaceOrderRequest request) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new RestaurantNotFoundException(request.getRestaurantId()));

        if (restaurant.getIsOpen() != null && !restaurant.getIsOpen()) {
            throw new RestaurantClosedException(restaurant.getName());
        }

        if (user.getLocation() == null || restaurant.getLocation() == null || !user.getLocation().getId().equals(restaurant.getLocation().getId())) {
            throw new RuntimeException("You can only order from restaurants in your assigned location.");
        }

        // Calculate total
        double totalAmount = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();
        List<FoodItems> foodItemsList = new ArrayList<>();

        for (PlaceOrderRequest.PlaceOrderItemRequest itemReq : request.getItems()) {
            FoodItems foodItem = foodItemsRepository.findById(itemReq.getFoodItemId())
                    .orElseThrow(() -> new FoodItemsNotFoundException(itemReq.getFoodItemId()));

            double itemPrice = foodItem.getPrice();
            totalAmount += itemPrice * itemReq.getQuantity();
            foodItemsList.add(foodItem);

            OrderItem orderItem = OrderItem.builder()
                    .foodItem(foodItem)
                    .quantity(itemReq.getQuantity())
                    .price(itemPrice)
                    .itemName(foodItem.getName())
                    .build();
            orderItems.add(orderItem);
        }

        // Handle coupon
        Double discountAmount = 0.0;
        Coupon coupon = null;
        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            coupon = couponRepository.findActiveByCode(request.getCouponCode().toUpperCase(), LocalDateTime.now())
                    .orElse(null);
            if (coupon != null && coupon.isValid() && coupon.meetsMinimumOrderValue(totalAmount)) {
                discountAmount = coupon.calculateDiscount(totalAmount);
            }
        }

        double finalAmount = totalAmount - discountAmount;

        Orders order = Orders.builder()
                .user(user)
                .restaurant(restaurant)
                .status(OrderStatus.PLACED)
                .paymentStatus(PaymentStatus.PENDING)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .deliveryFee(0.0)
                .finalAmount(finalAmount)
                .coupon(coupon)
                .specialInstructions(request.getSpecialInstructions())
                .foodItems(foodItemsList)
                .build();

        Orders savedOrder = ordersRepository.save(order);

        // Save order items
        for (OrderItem item : orderItems) {
            item.setOrder(savedOrder);
        }
        orderItemRepository.saveAll(orderItems);
        savedOrder.setOrderItems(orderItems);

        // Increment coupon usage
        if (coupon != null) {
            coupon.setUsedCount(coupon.getUsedCount() + 1);
            couponRepository.save(coupon);
        }

        log.info("Direct order placed: {} for user: {}", savedOrder.getId(), userId);
        return toOrderResponseDTO(savedOrder);
    }

    /**
     * Get all orders (admin)
     */
    public Page<OrderResponseDTO> getAllOrdersDTO(Pageable pageable) {
        return ordersRepository.findAll(pageable)
                .map(this::toOrderResponseDTO);
    }

    public Page<Orders> getAllOrders(Pageable pageable) {
        return ordersRepository.findAll(pageable);
    }

    /**
     * Get order by ID
     */
    public Orders getOrderById(UUID orderId) {
        return ordersRepository.findById(orderId)
                .orElseThrow(() -> new OrdersNotFoundException(orderId));
    }

    /**
     * Get order response DTO by ID
     */
    public OrderResponseDTO getOrderResponseById(UUID orderId) {
        Orders order = getOrderById(orderId);
        return toOrderResponseDTO(order);
    }

    /**
     * Get orders for a specific user
     */
    public Page<OrderResponseDTO> getOrdersByUserId(UUID userId, Pageable pageable) {
        return ordersRepository.findByUserIdOrderByOrderDateDesc(userId, pageable)
                .map(this::toOrderResponseDTO);
    }

    /**
     * Get orders for a specific restaurant
     */
    public Page<OrderResponseDTO> getOrdersByRestaurantId(UUID restaurantId, Pageable pageable) {
        return ordersRepository.findByRestaurantIdOrderByOrderDateDesc(restaurantId, pageable)
                .map(this::toOrderResponseDTO);
    }

    /**
     * Get today's orders for a restaurant
     */
    public Page<OrderResponseDTO> getTodayOrdersByRestaurant(UUID restaurantId, Pageable pageable) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return ordersRepository.findTodayOrdersByRestaurant(restaurantId, startOfDay, endOfDay, pageable)
                .map(this::toOrderResponseDTO);
    }

    /**
     * Get orders by status for a restaurant
     */
    public Page<OrderResponseDTO> getOrdersByRestaurantAndStatus(UUID restaurantId, OrderStatus status, Pageable pageable) {
        return ordersRepository.findByRestaurantIdAndStatus(restaurantId, status, pageable)
                .map(this::toOrderResponseDTO);
    }

    /**
     * Update order status
     */
    @Transactional
    public OrderResponseDTO updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        Orders order = getOrderById(orderId);

        order.setStatus(newStatus);
        LocalDateTime now = LocalDateTime.now();

        switch (newStatus) {
            case CONFIRMED -> order.setConfirmedAt(now);
            case PREPARING -> order.setPreparingAt(now);
            case READY -> order.setReadyAt(now);
            case PICKED_UP -> order.setPickedUpAt(now);
            case COMPLETED -> {
                order.setCompletedAt(now);
                order.setPaymentStatus(PaymentStatus.COMPLETED);
            }
            case CANCELLED -> order.setCancelledAt(now);
            default -> {}
        }

        Orders updated = ordersRepository.save(order);
        log.info("Order {} status updated to {}", orderId, newStatus);
        return toOrderResponseDTO(updated);
    }

    /**
     * Cancel order
     */
    @Transactional
    public OrderResponseDTO cancelOrder(UUID orderId, UUID userId, String reason) {
        Orders order = getOrderById(orderId);

        // Validate user owns the order
        if (!order.getUser().getId().equals(userId)) {
            throw new OrderCancellationException("You can only cancel your own orders.");
        }

        // Can only cancel PLACED or CONFIRMED orders
        if (order.getStatus() != OrderStatus.PLACED && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new OrderCancellationException(
                    "Cannot cancel order in '" + order.getStatus() + "' status. Order can only be cancelled when PLACED or CONFIRMED.");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        order.setCancellationReason(reason);

        // If payment was completed, mark for refund
        if (order.getPaymentStatus() == PaymentStatus.COMPLETED) {
            order.setPaymentStatus(PaymentStatus.REFUNDED);
        }

        Orders updated = ordersRepository.save(order);
        log.info("Order {} cancelled by user {} - reason: {}", orderId, userId, reason);
        return toOrderResponseDTO(updated);
    }

    /**
     * Update order by ID (legacy method compatibility)
     */
    @Transactional
    public Orders updateOrderById(UUID orderId, OrdersDTO ordersDTO) {
        Orders order = getOrderById(orderId);
        if (ordersDTO.getStatus() != null) {
            order.setStatus(OrderStatus.valueOf(ordersDTO.getStatus()));
        }
        return ordersRepository.save(order);
    }

    /**
     * Create order from DTO (legacy method)
     */
    @Transactional
    public Orders createOrderFromDTO(OrdersDTO ordersDTO) {
        Orders order = new Orders();
        order.setStatus(OrderStatus.PLACED);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setTotalAmount(0.0);
        order.setFinalAmount(0.0);
        order.setDiscountAmount(0.0);
        order.setDeliveryFee(0.0);

        if (ordersDTO.getRestaurant() != null && ordersDTO.getRestaurant().getId() != null) {
            Restaurant restaurant = restaurantRepository.findById(ordersDTO.getRestaurant().getId())
                    .orElseThrow(() -> new RestaurantNotFoundException(ordersDTO.getRestaurant().getId()));
            order.setRestaurant(restaurant);
        }

        return ordersRepository.save(order);
    }

    /**
     * Get orders by status (admin)
     */
    public Page<OrderResponseDTO> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        return ordersRepository.findByStatus(status, pageable)
                .map(this::toOrderResponseDTO);
    }

    /**
     * Convert Orders entity to OrderResponseDTO
     */
    public OrderResponseDTO toOrderResponseDTO(Orders order) {
        List<OrderItemDTO> itemDTOs = new ArrayList<>();

        // Try to use orderItems first, fall back to foodItems
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            itemDTOs = order.getOrderItems().stream()
                    .map(item -> OrderItemDTO.builder()
                            .id(item.getId())
                            .foodItemId(item.getFoodItem() != null ? item.getFoodItem().getId() : null)
                            .itemName(item.getItemName())
                            .quantity(item.getQuantity())
                            .price(item.getPrice())
                            .imageUrl(item.getFoodItem() != null ? item.getFoodItem().getImageUrl() : null)
                            .build())
                    .collect(Collectors.toList());
        } else if (order.getFoodItems() != null) {
            itemDTOs = order.getFoodItems().stream()
                    .map(fi -> OrderItemDTO.builder()
                            .foodItemId(fi.getId())
                            .itemName(fi.getName())
                            .quantity(1)
                            .price((double) fi.getPrice())
                            .imageUrl(fi.getImageUrl())
                            .build())
                    .collect(Collectors.toList());
        }

        return OrderResponseDTO.builder()
                .id(order.getId())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .username(order.getUser() != null ? order.getUser().getUsername() : null)
                .userEmail(order.getUser() != null ? order.getUser().getEmail() : null)
                .restaurantId(order.getRestaurant() != null ? order.getRestaurant().getId() : null)
                .restaurantName(order.getRestaurant() != null ? order.getRestaurant().getName() : null)
                .restaurantImageUrl(order.getRestaurant() != null ? order.getRestaurant().getImageUrl() : null)
                .items(itemDTOs)
                .totalAmount(order.getTotalAmount())
                .discountAmount(order.getDiscountAmount())
                .deliveryFee(order.getDeliveryFee())
                .finalAmount(order.getFinalAmount())
                .couponCode(order.getCoupon() != null ? order.getCoupon().getCode() : null)
                .couponDiscount(order.getDiscountAmount())
                .razorpayOrderId(order.getRazorpayOrderId())
                .razorpayPaymentId(order.getRazorpayPaymentId())
                .orderDate(order.getOrderDate())
                .confirmedAt(order.getConfirmedAt())
                .preparingAt(order.getPreparingAt())
                .readyAt(order.getReadyAt())
                .pickedUpAt(order.getPickedUpAt())
                .completedAt(order.getCompletedAt())
                .cancelledAt(order.getCancelledAt())
                .cancellationReason(order.getCancellationReason())
                .specialInstructions(order.getSpecialInstructions())
                .build();
    }
}
