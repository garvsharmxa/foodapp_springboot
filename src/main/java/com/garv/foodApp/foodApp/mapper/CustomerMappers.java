package com.garv.foodApp.foodApp.mapper;

import com.garv.foodApp.foodApp.DTO.CustomerDTO;
import com.garv.foodApp.foodApp.Entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerMappers {

    // Entity -> DTO
    CustomerDTO toCustomerDTO(Customer customer);

    // DTO -> Entity
    Customer toCustomer(CustomerDTO customerDTO);

    // For PATCH (partial update)
    void updateCustomerFromDTO(CustomerDTO dto, @MappingTarget Customer customer);

    // For PUT (full update)
    void fullUpdateCustomerFromDTO(CustomerDTO dto, @MappingTarget Customer customer);
}
