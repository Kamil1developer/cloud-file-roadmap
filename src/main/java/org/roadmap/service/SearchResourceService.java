package org.roadmap.service;

import lombok.RequiredArgsConstructor;
import org.roadmap.dto.response.SearchResourceResponse;
import org.roadmap.mapper.ResourceMapper;
import org.roadmap.storage.MinioStorage;
import org.roadmap.storage.MinioStorageImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchResourceService {
    private final MinioStorage storage;
    private final ResourceMapper mapper;

    public List<SearchResourceResponse> search(String query){
        return mapper.toSearchResponse(storage.findResourcesByName(query));
    }
}
