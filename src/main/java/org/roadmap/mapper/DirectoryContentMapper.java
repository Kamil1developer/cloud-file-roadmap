package org.roadmap.mapper;

import org.mapstruct.Mapper;
import org.roadmap.dto.response.DirectoryContentResponse;
import org.roadmap.storage.model.StorageResource;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DirectoryContentMapper {
    public List<DirectoryContentResponse> toDirectoryContentResponse(List<StorageResource> resource);
}
