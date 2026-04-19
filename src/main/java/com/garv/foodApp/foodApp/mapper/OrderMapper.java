package com.garv.foodApp.foodApp.mapper;

import com.garv.foodApp.foodApp.DTO.OrdersDTO;
import com.garv.foodApp.foodApp.Entity.Orders;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = { RestaurantSummaryMapper.class })
public interface OrderMapper {

    @Mapping(source = "user.id", target = "customerId")
    @Mapping(target = "restaurant", source = "restaurant")
    @Mapping(target = "foodItems", source = "foodItems")
    OrdersDTO toOrdersDTO(Orders orders);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "razorpayOrderId", ignore = true)
    @Mapping(target = "razorpayPaymentId", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    @Mapping(target = "orderDate", ignore = true)
    @Mapping(target = "specialInstructions", ignore = true)
    @Mapping(target = "readyAt", ignore = true)
    @Mapping(target = "pickedUpAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "coupon", ignore = true)
    @Mapping(target = "discountAmount", ignore = true)
    @Mapping(target = "deliveryFee", ignore = true)
    @Mapping(target = "finalAmount", ignore = true)
    @Mapping(target = "confirmedAt", ignore = true)
    @Mapping(target = "preparingAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "cancelledAt", ignore = true)
    @Mapping(target = "cancellationReason", ignore = true)
    @Mapping(target = "statusHistory", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    Orders toOrders(OrdersDTO ordersDTO);

    List<OrdersDTO> toOrdersDTOList(List<Orders> orders);
}
