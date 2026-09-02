package org.roadmap.dto.response;

public record UploadResponse(
        String path,
        String name,
        long size,
        String type
) {

}
