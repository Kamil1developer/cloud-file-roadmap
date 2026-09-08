package org.roadmap.service;

import lombok.RequiredArgsConstructor;
import org.roadmap.dto.response.UploadResponse;
import org.roadmap.mapper.ResourceMapper;
import org.roadmap.storage.MinioStorage;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetResourceService {
    private final MinioStorage storage;
    private final ResourceMapper mapper;

    public UploadResponse get(String path){
        return mapper.
                toUploadResponse(storage.getResourceByPath(path));
    }
}
