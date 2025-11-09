package com.garv.foodApp.foodApp.mapper;

import com.garv.foodApp.foodApp.DTO.UserDTO;
import com.garv.foodApp.foodApp.Entity.Users;   // <-- Correct entity import
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Entity -> DTO
    UserDTO toUserDTO(Users user);

    // DTO -> Entity
    Users toUser(UserDTO userDTO);
}
