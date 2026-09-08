package org.roadmap.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.roadmap.service.DeleteResourceService;
import org.roadmap.service.GetResourceService;
import org.roadmap.service.UploadResourceService;
import org.roadmap.dto.response.UploadResponse;
import org.simpleframework.xml.core.Validate;
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
    private final UploadResourceService uploadService;
    private final DeleteResourceService deleteService;
    private final GetResourceService getService;

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

    @Operation(summary = "getResource")
    @GetMapping
    public ResponseEntity<UploadResponse> get(@RequestParam("path")
                                                  @Pattern(regexp = "^(?!/)(?!.*//)(?!.*\\\\\\\\)[^\\\\p{Cntrl}]+$", message = "невалидный или отсутствующий путь")
                                                  @NotBlank
                                                  String path) {
        UploadResponse uploadResponse = getService.get(path);

        return ResponseEntity.status(HttpStatus.OK)
                .body(uploadResponse);
    }
}
