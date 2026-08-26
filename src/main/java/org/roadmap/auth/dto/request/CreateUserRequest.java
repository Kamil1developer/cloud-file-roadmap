package org.roadmap.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor

public class CreateUserRequest {
    @Pattern(
            regexp = "^[A-Za-z@.]+$",
            message = "Имя пользователя должно быть на латинском")
    @NotBlank
    private final String username;

    @Pattern(
            regexp = "^[A-Za-z@.]+$",
            message = "Пароль должен быть на латинском")
    @NotBlank
    private final String password;
}
