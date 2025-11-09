package com.garv.foodApp.foodApp.Service;
import com.garv.foodApp.foodApp.DTO.OrdersDTO;
import com.garv.foodApp.foodApp.Entity.Orders;
import com.garv.foodApp.foodApp.Exception.CustomerNotFoundException;
import com.garv.foodApp.foodApp.Exception.OrdersNotFoundException;
import com.garv.foodApp.foodApp.Repository.CustomerRepository;
import com.garv.foodApp.foodApp.Repository.OrdersRepository;
import com.garv.foodApp.foodApp.mapper.OrderMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServices {

    private final OrdersRepository ordersRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapper orderMapper;

    /**
     * Constructor injection ensures that required dependencies
     * are always provided and makes testing easier.
     */
    public OrderServices(OrdersRepository ordersRepository, CustomerRepository customerRepository, OrderMapper orderMapper) {
        this.ordersRepository = ordersRepository;
        this.customerRepository = customerRepository;
        this.orderMapper = orderMapper;
    }

    /**
     * CREATE order from DTO.
     * Converts DTO → Entity using MapStruct,
     * sets customer if customerId is provided, then saves to DB.
     */
    public Orders createOrderFromDTO(OrdersDTO orderDTO) {
        Orders order = orderMapper.toOrders(orderDTO);

        // If customerId is present, fetch customer and set it to the order
        customerRepository.findById(orderDTO.getCustomerId())
                .ifPresent(order::setCustomer);

        return ordersRepository.save(order); // Save order and return saved entity
    }

    /**
     * GET all orders.
     * Returns list of all order entities from the database.
     */
    public List<Orders> getAllOrders() {
        return ordersRepository.findAll();
    }

    /**
     * GET order by ID.
     * Throws OrdersNotFoundException if order with given ID doesn't exist.
     */
    public Orders getOrderById(Long id) {
        return ordersRepository.findById(id)
                .orElseThrow(() -> new OrdersNotFoundException(id));
    }

    /**
     * GET orders by user/customer ID.
     * Returns list of orders for a specific customer.
     * Throws CustomerNotFoundException if no orders exist for that customer (optional, handle in controller if needed).
     */
    public List<Orders> getOrderByUserId(Long customerId) {
        return ordersRepository.findAllOrdersByCustomerId(customerId)
                .stream()
                .toList(); // Convert to List (Java 16+)
    }

    /**
     * DELETE order by ID.
     * Checks if order exists, throws exception if not, then deletes the order.
     */
    public void deleteOrderById(Long id) {
        if (!ordersRepository.existsById(id)) throw new OrdersNotFoundException(id);
        ordersRepository.deleteById(id);
    }

    /**
     * UPDATE order by ID (status and/or customer).
     * - Updates customer if customerId is provided in DTO.
     * - Updates order status if provided in DTO.
     * - Saves updated order to DB and returns it.
     */
    public Orders updateOrderById(Long id, OrdersDTO orderDTO) {
        Orders order = getOrderById(id); // Fetch existing order

        // Update customer if customerId is present in DTO
        if (orderDTO.getCustomerId() != null) {
            order.setCustomer(customerRepository.findById(orderDTO.getCustomerId())
                    .orElseThrow(() -> new CustomerNotFoundException(orderDTO.getCustomerId())));
        }

        // Update status if present
        if (orderDTO.getStatus() != null) order.setStatus(orderDTO.getStatus());

        return ordersRepository.save(order); // Save and return updated order
    }
}
