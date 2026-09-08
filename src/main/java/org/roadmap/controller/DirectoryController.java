package org.roadmap.controller;

import lombok.RequiredArgsConstructor;
import org.roadmap.dto.response.CreatedDirectoryResponse;
import org.roadmap.dto.response.DirectoryContentResponse;
import org.roadmap.service.DirectoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<List<DirectoryContentResponse>> getDirectoryContents(@RequestParam("path") String path){
        List<DirectoryContentResponse> response = directoryService.getContent(path);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping
    public ResponseEntity<CreatedDirectoryResponse> createDirectory(@RequestParam("path") String path){
        CreatedDirectoryResponse response = directoryService.createDirectory(path);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
