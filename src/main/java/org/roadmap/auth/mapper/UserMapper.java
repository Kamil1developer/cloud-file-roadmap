package org.roadmap.auth.mapper;

import org.mapstruct.Mapper;
import org.roadmap.auth.dto.RegisterUserDto;
import org.roadmap.auth.dto.request.CreateUserRequest;
import org.roadmap.auth.dto.response.UserResponse;
import org.roadmap.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    public RegisterUserDto toRegisterUserDto(CreateUserRequest request);
    public UserResponse toUserResponse(User user);
}
