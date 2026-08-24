package org.roadmap.controller;

import lombok.RequiredArgsConstructor;
import org.roadmap.dto.RegisterUserDto;
import org.roadmap.dto.request.CreateUserRequest;
import org.roadmap.entity.User;
import org.roadmap.mapper.UserMapper;
import org.roadmap.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserMapper userMapper;
    @PostMapping("/sign-up")
    public void signUp(@RequestBody CreateUserRequest request){
        RegisterUserDto dto = userMapper.toRegisterUserDto(request);

        authService.createUser(dto);
    }

}
