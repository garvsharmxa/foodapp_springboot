package com.garv.foodApp.foodApp.mapper;

import com.garv.foodApp.foodApp.DTO.UserDTO;
import com.garv.foodApp.foodApp.Entity.Users; // <-- Correct entity import
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Entity -> DTO
    UserDTO toUserDTO(Users user);

    // DTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "otp", ignore = true)
    @Mapping(target = "otpGeneratedTime", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    Users toUser(UserDTO userDTO);
}
