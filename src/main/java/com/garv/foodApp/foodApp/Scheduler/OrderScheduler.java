package com.garv.foodApp.foodApp.Scheduler;
import com.garv.foodApp.foodApp.Repository.OrdersRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.garv.foodApp.foodApp.Entity.Orders;


import java.util.List;

@Service
public class OrderScheduler {

    private final OrdersRepository ordersRepository;

    OrderScheduler(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    @Scheduled(initialDelay = 10000000,fixedDelay = 500000000)
    public void processOrder() {
        System.out.println("Processing orders");
        List<Orders> orders = ordersRepository.findByStatus("PENDING");
        orders.forEach(order -> {
            order.setStatus("CONFIRMED");
            ordersRepository.save(order);
        });
        System.out.println("Orders processed successfully" + orders.size());
    }

}
