package org.roadmap.mapper;

import org.mapstruct.Mapper;
import org.roadmap.dto.RegisterUserDto;
import org.roadmap.dto.request.CreateUserRequest;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface UserMapper {
    public RegisterUserDto toRegisterUserDto(CreateUserRequest request);
}
