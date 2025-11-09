package com.garv.foodApp.foodApp.Repository;

import com.garv.foodApp.foodApp.Entity.Orders;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    List<Orders> findAllOrdersByCustomerId(Long customerId); // Add this for multiple orders

    List<Orders> findByStatus(String pending);

}