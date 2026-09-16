package org.roadmap.service;

import lombok.Data;
import org.roadmap.storage.MinioStorage;
import org.springframework.stereotype.Service;

@Service
@Data
public class DeleteResourceService {
    private final MinioStorage minioStorage;
    private final UserStoragePathResolver pathResolver;

    public void delete(String username, String path){
        String storagePath =
                pathResolver.toStoragePath(username, path);

        if (path.endsWith("/")) {
            minioStorage.deleteByPrefix(storagePath);
        } else {
            minioStorage.deleteByPath(storagePath);
        }
    }
}
