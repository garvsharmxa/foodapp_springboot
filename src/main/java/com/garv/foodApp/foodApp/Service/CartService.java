package com.garv.foodApp.foodApp.Service;

import com.garv.foodApp.foodApp.DTO.CartDTO;
import com.garv.foodApp.foodApp.DTO.CartItemDTO;
import com.garv.foodApp.foodApp.Entity.Cart;
import com.garv.foodApp.foodApp.Entity.CartItem;
import com.garv.foodApp.foodApp.Entity.Customer;
import com.garv.foodApp.foodApp.Entity.FoodItems;
import com.garv.foodApp.foodApp.Repository.CartItemRepository;
import com.garv.foodApp.foodApp.Repository.CartRepository;
import com.garv.foodApp.foodApp.Repository.CustomerRepository;
import com.garv.foodApp.foodApp.Repository.FoodItemsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final FoodItemsRepository foodItemsRepository;

    @Transactional
    public CartDTO getOrCreateCart(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));
        
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .customer(customer)
                            .items(new ArrayList<>())
                            .totalAmount(0.0)
                            .build();
                    return cartRepository.save(newCart);
                });
        
        return convertToDTO(cart);
    }

    @Transactional
    public CartDTO addItemToCart(Long customerId, Long foodItemId, Integer quantity) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    Customer customer = customerRepository.findById(customerId)
                            .orElseThrow(() -> new RuntimeException("Customer not found"));
                    return cartRepository.save(Cart.builder()
                            .customer(customer)
                            .items(new ArrayList<>())
                            .totalAmount(0.0)
                            .build());
                });
        
        FoodItems foodItem = foodItemsRepository.findById(foodItemId)
                .orElseThrow(() -> new RuntimeException("Food item not found with id: " + foodItemId));
        
        CartItem cartItem = cartItemRepository.findByCartIdAndFoodItemId(cart.getId(), foodItemId)
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + quantity);
                    existing.setPrice(existing.getQuantity() * (double) foodItem.getPrice());
                    return existing;
                })
                .orElseGet(() -> CartItem.builder()
                        .cart(cart)
                        .foodItem(foodItem)
                        .quantity(quantity)
                        .price(quantity * (double) foodItem.getPrice())
                        .build());
        
        cartItemRepository.save(cartItem);
        updateCartTotal(cart);
        
        return convertToDTO(cartRepository.findById(cart.getId()).orElseThrow());
    }

    @Transactional
    public CartDTO removeItemFromCart(Long customerId, Long cartItemId) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Cart not found for customer"));
        
        cartItemRepository.deleteById(cartItemId);
        updateCartTotal(cart);
        
        return convertToDTO(cartRepository.findById(cart.getId()).orElseThrow());
    }

    @Transactional
    public CartDTO updateItemQuantity(Long customerId, Long cartItemId, Integer quantity) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Cart not found for customer"));
        
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItem.setPrice(quantity * (double) cartItem.getFoodItem().getPrice());
            cartItemRepository.save(cartItem);
        }
        
        updateCartTotal(cart);
        return convertToDTO(cartRepository.findById(cart.getId()).orElseThrow());
    }

    @Transactional
    public void clearCart(Long customerId) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Cart not found for customer"));
        
        cartItemRepository.deleteAll(cart.getItems());
        cart.setTotalAmount(0.0);
        cartRepository.save(cart);
    }

    private void updateCartTotal(Cart cart) {
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        Double total = items.stream()
                .mapToDouble(CartItem::getPrice)
                .sum();
        cart.setTotalAmount(total);
        cartRepository.save(cart);
    }

    private CartDTO convertToDTO(Cart cart) {
        List<CartItemDTO> itemDTOs = cart.getItems() != null ? 
                cart.getItems().stream()
                        .map(this::convertItemToDTO)
                        .toList() : new ArrayList<>();
        
        return CartDTO.builder()
                .id(cart.getId())
                .customerId(cart.getCustomer() != null ? cart.getCustomer().getId() : null)
                .items(itemDTOs)
                .totalAmount(cart.getTotalAmount())
                .build();
    }

    private CartItemDTO convertItemToDTO(CartItem item) {
        return CartItemDTO.builder()
                .id(item.getId())
                .foodItemId(item.getFoodItem() != null ? item.getFoodItem().getId() : null)
                .foodItemName(item.getFoodItem() != null ? item.getFoodItem().getName() : null)
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build();
    }
}
