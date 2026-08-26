package org.roadmap.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.roadmap.auth.dto.RegisterUserDto;
import org.roadmap.auth.dto.request.CreateUserRequest;
import org.roadmap.auth.dto.response.UserResponse;
import org.roadmap.auth.mapper.UserMapper;
import org.roadmap.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthController {
    private final AuthService authService;
    private final UserMapper userMapper;

    @PostMapping("/sign-up")
    @Operation(summary = "Регистрация пользователя ")
    public ResponseEntity<UserResponse> signUp(@Valid @RequestBody CreateUserRequest request){
        RegisterUserDto registerUserDto = userMapper.toRegisterUserDto(request);

        UserResponse userResponse = authService.createUser(registerUserDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userResponse);
    }

}
