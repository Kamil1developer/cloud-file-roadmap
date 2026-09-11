package org.roadmap.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.roadmap.dto.response.MoveOrRenameResponse;
import org.roadmap.dto.response.SearchResourceResponse;
import org.roadmap.service.*;
import org.roadmap.dto.response.UploadResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("resource")
@RequiredArgsConstructor
public class ResourceController {
    private final UploadResourceService uploadService;
    private final DeleteResourceService deleteService;
    private final GetResourceService getService;
    private final MoveOrRenameService moveOrRenameService;
    private final DownloadResourceService downloadService;
    private final SearchResourceService searchService;

    @Operation(summary = "upload")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<UploadResponse>> upload(
            @RequestParam("path") String path,
            @RequestPart("object") List<MultipartFile> file) throws IOException {
        Optional<List<UploadResponse>> optionalUploadResponse = uploadService.upload(path,file);
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


    @Operation(summary = "move")
    @PostMapping("/move")
    public ResponseEntity<MoveOrRenameResponse> moveOrRename(
            @RequestParam("from") String from,
            @RequestParam("to") String to) throws IOException {

        MoveOrRenameResponse response = moveOrRenameService.moveOrRename(from, to);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);

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
                                                  @Pattern(regexp = "^(?!/)(?!.*//)(?!.*\\\\)[^\\\\\\p{Cntrl}]+$", message = "невалидный или отсутствующий путь")
                                                  @NotBlank
                                                  String path) {
        UploadResponse uploadResponse = getService.get(path);

        return ResponseEntity.status(HttpStatus.OK)
                .body(uploadResponse);
    }

    @Operation(summary = "downloadResource")
    @GetMapping("/download")
    public ResponseEntity<StreamingResponseBody> download(@RequestParam("path")
                                                              @NotBlank
                                                              @Pattern(regexp = "^(?!/)(?!.*//)(?!.*\\\\)[^\\\\\\p{Cntrl}]+$", message = "невалидный или отсутствующий путь")
                                                       String path) throws IOException{

        InputStream inputStream = downloadService.downloadResource(path);

        StreamingResponseBody responseBody = outputStream -> {
            try (inputStream) {
                inputStream.transferTo(outputStream);
            }
        };

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseBody);
    }

    @GetMapping("/search")
    public ResponseEntity<List<SearchResourceResponse>> search(@RequestParam("query")
                                                                   @NotBlank
                                                                   String query){
        List<SearchResourceResponse> searchResourceResponses = searchService.search(query);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(searchResourceResponses);
    }
}
