package org.roadmap.auth.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("resource")
public class ResourceController {
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity upload(
            @RequestParam("path") String path,
            @RequestPart("file") MultipartFile  multipartFile){


    }
}
