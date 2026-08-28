package org.roadmap.config;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenApiCustomizer logoutOpenApiCustomizer() {
        return openApi -> openApi.path(
                "/sign-out",
                new PathItem().post(
                        new Operation()
                                .tags(List.of("Logout"))
                                .summary("Выход из аккаунта")
                                .description(
                                        "Завершает текущую пользовательскую сессию"
                                )
                                .responses(
                                        new ApiResponses()
                                                .addApiResponse(
                                                        "200",
                                                        new ApiResponse()
                                                                .description("Выход выполнен успешно")
                                                )
                                )
                )
        );
    }
}