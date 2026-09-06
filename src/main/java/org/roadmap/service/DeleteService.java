package org.roadmap.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.roadmap.storage.MinioStorage;
import org.springframework.stereotype.Service;

@Service
@Data
public class DeleteService {
    private final MinioStorage minioStorage;

    public void delete(String path){
        minioStorage.deleteByPath(path);
    }
}
