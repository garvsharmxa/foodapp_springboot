package com.garv.foodApp.foodApp.mapper;

import com.garv.foodApp.foodApp.DTO.OrdersDTO;
import com.garv.foodApp.foodApp.Entity.Orders;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    // Map customer.id in entity → customerId in DTO
    @Mapping(source = "customer.id", target = "customerId")
    OrdersDTO toOrdersDTO(Orders orders);

    // Convert DTO → Entity (customer handled in service)
    Orders toOrders(OrdersDTO ordersDTO);

    List<OrdersDTO> toOrdersDTOList(List<Orders> orders);
}
