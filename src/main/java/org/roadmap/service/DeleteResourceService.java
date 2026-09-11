package org.roadmap.service;

import lombok.Data;
import org.roadmap.storage.MinioStorage;
import org.springframework.stereotype.Service;

@Service
@Data
public class DeleteResourceService {
    private final MinioStorage minioStorage;

    public void delete(String path){
        minioStorage.deleteByPrefix(path);
    }
}
