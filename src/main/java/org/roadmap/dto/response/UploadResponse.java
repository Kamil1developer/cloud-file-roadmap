package org.roadmap.dto.response;

public record UploadResponse(
        String path,
        String name,
        int size,
        String type
) {

}
