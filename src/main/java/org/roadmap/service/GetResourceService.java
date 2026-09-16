package org.roadmap.service;

import lombok.RequiredArgsConstructor;
import org.roadmap.dto.response.UploadResponse;
import org.roadmap.mapper.ResourceMapper;
import org.roadmap.storage.MinioStorage;
import org.roadmap.storage.model.StorageResource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetResourceService {
    private final MinioStorage storage;
    private final ResourceMapper mapper;
    private final UserStoragePathResolver pathResolver;

    public UploadResponse get(String username, String path){
        StorageResource resource = storage.getResourceByPath(
                pathResolver.toStoragePath(username, path)
        );

        return mapper.toUploadResponse(
                pathResolver.toPublicResource(username, resource)
        );
    }
}
