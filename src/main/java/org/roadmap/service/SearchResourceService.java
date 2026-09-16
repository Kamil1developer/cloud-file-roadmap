package org.roadmap.service;

import lombok.RequiredArgsConstructor;
import org.roadmap.dto.response.SearchResourceResponse;
import org.roadmap.mapper.ResourceMapper;
import org.roadmap.storage.MinioStorage;
import org.roadmap.storage.model.StorageResource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchResourceService {
    private final MinioStorage storage;
    private final ResourceMapper mapper;
    private final UserStoragePathResolver pathResolver;

    public List<SearchResourceResponse> search(String username, String query){
        List<StorageResource> resources = storage.findResourcesByName(
                        pathResolver.userRoot(username),
                        query
                ).stream()
                .map(resource -> pathResolver.toPublicResource(username, resource))
                .toList();

        return mapper.toSearchResponse(resources);
    }
}
