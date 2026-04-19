package com.garv.foodApp.foodApp.mapper;

import com.garv.foodApp.foodApp.DTO.CustomerDTO;
import com.garv.foodApp.foodApp.Entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerMappers {

    // Entity -> DTO
    @Mapping(source = "username", target = "name")
    CustomerDTO toCustomerDTO(Users user);

    // DTO -> Entity
    @Mapping(source = "name", target = "username")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "otp", ignore = true)
    @Mapping(target = "otpGeneratedTime", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    Users toUsers(CustomerDTO customerDTO);

    // For PATCH (partial update)
    @Mapping(source = "name", target = "username")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "otp", ignore = true)
    @Mapping(target = "otpGeneratedTime", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    void updateUsersFromDTO(CustomerDTO dto, @MappingTarget Users user);

    // For PUT (full update)
    @Mapping(source = "name", target = "username")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "otp", ignore = true)
    @Mapping(target = "otpGeneratedTime", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    void fullUpdateUsersFromDTO(CustomerDTO dto, @MappingTarget Users user);
}
