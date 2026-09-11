package org.roadmap.storage.model;


import com.fasterxml.jackson.annotation.JsonInclude;

public record StorageResource(
        String path,
        String name,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Long size,
        String type
){
}
