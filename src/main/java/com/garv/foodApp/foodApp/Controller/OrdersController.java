package com.garv.foodApp.foodApp.Controller;

import com.garv.foodApp.foodApp.DTO.OrdersDTO;
import com.garv.foodApp.foodApp.Entity.Orders;
import com.garv.foodApp.foodApp.Service.OrderServices;
import com.garv.foodApp.foodApp.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrderServices orderServices;
    private final OrderMapper orderMapper;

    @PostMapping
    public OrdersDTO createOrder(@RequestBody OrdersDTO orderDTO) {
        Orders savedOrder = orderServices.createOrderFromDTO(orderDTO);
        return orderMapper.toOrdersDTO(savedOrder);
    }

    @GetMapping
    public List<OrdersDTO> getAllOrders() {
        List<Orders> orders = orderServices.getAllOrders();
        return orderMapper.toOrdersDTOList(orders);
    }

    @GetMapping("/{id}")
    public OrdersDTO getOrderById(@PathVariable Long id) {
        Orders order = orderServices.getOrderById(id);
        return orderMapper.toOrdersDTO(order);
    }

    @GetMapping("/customer/{customerId}")
    public List<OrdersDTO> getOrdersByUserId(@PathVariable Long customerId) {
        List<Orders> orders = orderServices.getOrderByUserId(customerId);
        return orderMapper.toOrdersDTOList(orders);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderServices.deleteOrderById(id);
    }

    @PatchMapping("/{id}")
    public OrdersDTO updateOrder(@PathVariable Long id, @RequestBody OrdersDTO orderDTO) {
        Orders savedOrder = orderServices.updateOrderById(id, orderDTO);
        return orderMapper.toOrdersDTO(savedOrder);
    }
}
