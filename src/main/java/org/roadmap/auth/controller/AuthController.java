package org.roadmap.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.roadmap.auth.dto.RegisterUserDto;
import org.roadmap.auth.dto.request.CreateUserRequest;
import org.roadmap.auth.dto.request.SignInRequest;
import org.roadmap.auth.dto.response.UserResponse;
import org.roadmap.auth.mapper.UserMapper;
import org.roadmap.auth.service.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
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
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    @PostMapping("/sign-up")
    @Operation(summary = "Регистрация пользователя ")
    public ResponseEntity<UserResponse> signUp(@Valid @RequestBody CreateUserRequest request){
        RegisterUserDto registerUserDto = userMapper.toRegisterUserDto(request);

        UserResponse userResponse = authService.createUser(registerUserDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userResponse);
    }

    @PostMapping("/sign-in")
    @Operation(summary = "Аутентификация пользователя")
    public ResponseEntity<UserResponse> signIn(@Valid @RequestBody SignInRequest signInRequest,
                                               HttpServletRequest request,
                                               HttpServletResponse response){

        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.
                unauthenticated(
                        signInRequest.getUsername(),
                        signInRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationRequest);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);

        SecurityContextHolder.setContext(securityContext);

        securityContextRepository.saveContext(securityContext, request, response);


        return ResponseEntity.
                status(HttpStatus.OK)
                .body(new UserResponse(authentication.getName()));
    }

}
