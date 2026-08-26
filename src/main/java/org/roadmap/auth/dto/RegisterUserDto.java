package org.roadmap.auth.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RegisterUserDto {
    private final String username;
    private final String password;
}
