package org.roadmap.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

public record SearchResourceResponse(
        String path,
        String name,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Long size,
        String type
) {
}
