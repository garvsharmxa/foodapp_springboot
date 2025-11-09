package com.garv.foodApp.foodApp.Controller;
import com.garv.foodApp.foodApp.Entity.Orders;
import com.garv.foodApp.foodApp.Repository.OrdersRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class OrderStatusController {

    private final OrdersRepository repository;

    public OrderStatusController(OrdersRepository repository) {
        this.repository = repository;
    }

    @MessageMapping("/update-order")
    @SendTo("/topic/order-status")
    public Orders updateOrder(Orders order) {
        return repository.findById(order.getId())
                .map(o -> { o.setStatus(order.getStatus()); return repository.save(o); })
                .orElse(null);
    }
}
