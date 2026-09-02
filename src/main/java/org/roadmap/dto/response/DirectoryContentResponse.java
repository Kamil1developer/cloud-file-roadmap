package org.roadmap.dto.response;

public record DirectoryContentResponse(
        String path,
        String name,
        long size,
        String type
){

}
