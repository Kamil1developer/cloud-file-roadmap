package org.roadmap.service;

import lombok.RequiredArgsConstructor;
import org.roadmap.dto.response.CreatedDirectoryResponse;
import org.roadmap.dto.response.DirectoryContentResponse;
import org.roadmap.mapper.DirectoryContentMapper;
import org.roadmap.storage.MinioStorage;
import org.roadmap.storage.model.DirectoryResource;
import org.roadmap.storage.model.StorageResource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectoryService {
    private final MinioStorage storage;
    private final DirectoryContentMapper mapper;
    private final UserStoragePathResolver pathResolver;

    public List<DirectoryContentResponse> getContent(String username, String path){
        List<StorageResource> resources = storage.getContentByDirectory(
                        pathResolver.toStoragePath(username, path)
                ).stream()
                .map(resource -> pathResolver.toPublicResource(username, resource))
                .toList();

        return mapper.toDirectoryContentResponse(resources);
    }


    public CreatedDirectoryResponse createDirectory(String username, String path) {
        DirectoryResource resource = storage.createDirectoryByPath(
                pathResolver.toStoragePath(username, path)
        );

        return mapper.toCreatedDirectoryResponse(
                pathResolver.toPublicResource(username, resource)
        );
    }
}
