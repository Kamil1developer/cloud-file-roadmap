package org.roadmap.service;

import lombok.RequiredArgsConstructor;
import org.roadmap.dto.response.DirectoryContentResponse;
import org.roadmap.mapper.DirectoryContentMapper;
import org.roadmap.storage.MinioStorage;
import org.roadmap.storage.model.StorageResource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectoryService {
    private final MinioStorage storage;
    private final DirectoryContentMapper mapper;

    public List<DirectoryContentResponse> getContent(String path){
        List<StorageResource> resource = storage.getContent(path);
        return mapper.toDirectoryContentResponse(resource);
    }


}
