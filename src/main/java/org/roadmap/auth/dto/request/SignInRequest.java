package org.roadmap.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SignInRequest {
    @Pattern(
            regexp = "^[A-Za-z@.]+$",
            message = "Имя пользователя должно быть на латинском")
    @NotBlank
    @Size(min = 3 , max = 10)
    public final String username;

    @Pattern(
            regexp = "^[A-Za-z@.]+$",
            message = "Пароль должен быть на латинском")
    @NotBlank
    public final String password;
}
