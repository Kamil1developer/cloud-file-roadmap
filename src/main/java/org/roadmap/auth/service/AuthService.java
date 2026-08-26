package org.roadmap.auth.service;

import lombok.RequiredArgsConstructor;
import org.roadmap.auth.dto.RegisterUserDto;
import org.roadmap.auth.dto.response.UserResponse;
import org.roadmap.auth.mapper.UserMapper;
import org.roadmap.entity.User;
import org.roadmap.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserResponse createUser(RegisterUserDto registerUserDto){
        String passwordHash = passwordEncoder.encode(registerUserDto.getPassword());
        String userName = registerUserDto.getUsername();

        User user = userRepository.save(new User(userName, passwordHash));

        return userMapper.toUserResponse(user);
    }
}
