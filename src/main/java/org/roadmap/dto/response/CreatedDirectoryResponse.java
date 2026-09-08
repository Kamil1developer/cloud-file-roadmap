package org.roadmap.dto.response;

public record CreatedDirectoryResponse(
        String path,
        String name,
        String type
) {
}
