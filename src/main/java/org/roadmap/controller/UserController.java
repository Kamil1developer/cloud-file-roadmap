package org.roadmap.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {
    @Operation(summary = "Текущий пользователь")
    @GetMapping("me")
    public ResponseEntity<Map<String, String>> getCurrentUser(Authentication authentication){

        return ResponseEntity.
                status(HttpStatus.OK)
                .body(Map.of("username", authentication.getName()));
    }
}
