package org.roadmap.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.roadmap.dto.response.CreatedDirectoryResponse;
import org.roadmap.dto.response.DirectoryContentResponse;
import org.roadmap.service.DirectoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("directory")
@RequiredArgsConstructor
public class DirectoryController {
    private final DirectoryService directoryService;

    @Operation(summary = "Получение содержимого директории")
    @GetMapping
    public ResponseEntity<List<DirectoryContentResponse>> getDirectoryContents(
            @RequestParam("path") String path,
            Authentication authentication) {
        List<DirectoryContentResponse> response = directoryService.getContent(
                authentication.getName(),
                path
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @Operation(summary = "Создание директории")
    @PostMapping
    public ResponseEntity<CreatedDirectoryResponse> createDirectory(
            @RequestParam("path") String path,
            Authentication authentication) {
        CreatedDirectoryResponse response = directoryService.createDirectory(
                authentication.getName(),
                path
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}
