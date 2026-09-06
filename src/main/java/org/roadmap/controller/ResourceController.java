package org.roadmap.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.roadmap.service.DeleteService;
import org.roadmap.service.UploadService;
import org.roadmap.dto.response.UploadResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("resource")
@RequiredArgsConstructor
public class ResourceController {
    private final UploadService uploadService;
    private final DeleteService deleteService;

    @Operation(summary = "upload")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<UploadResponse>> upload(
            @RequestParam("path") String path,
            @RequestPart("object") List<MultipartFile> file) throws IOException {
        Optional<List<UploadResponse>> optionalUploadResponse = uploadService.upload(file);
        if (optionalUploadResponse.isPresent()) {
            List<UploadResponse> uploadResponses = optionalUploadResponse.get();
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(uploadResponses);
        }
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .build();

    }


    @Operation(summary = "delete")
    @DeleteMapping
    public ResponseEntity delete(@RequestParam("path") String path){
        deleteService.delete(path);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
