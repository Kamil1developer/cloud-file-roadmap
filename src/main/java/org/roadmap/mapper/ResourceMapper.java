package org.roadmap.mapper;

import org.mapstruct.Mapper;
import org.roadmap.dto.response.UploadResponse;
import org.roadmap.storage.model.StorageResource;

@Mapper(componentModel = "spring")
public interface ResourceMapper {
    public UploadResponse toUploadResponse(StorageResource storageResource);

}
