package org.roadmap.mapper;

import org.mapstruct.Mapper;
import org.roadmap.dto.response.ResourceResponse;
import org.roadmap.dto.response.SearchResourceResponse;
import org.roadmap.dto.response.UploadResponse;
import org.roadmap.storage.model.StorageResource;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ResourceMapper {
    public UploadResponse toUploadResponse(StorageResource storageResource);
    public ResourceResponse toResourceResponse(StorageResource storageResource);
    public List<SearchResourceResponse> toSearchResponse(List<StorageResource> resources);


}
