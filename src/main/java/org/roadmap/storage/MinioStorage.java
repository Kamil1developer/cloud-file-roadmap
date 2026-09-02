package org.roadmap.storage;

import io.minio.errors.MinioException;
import org.roadmap.storage.dto.ObjectUploadRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface MinioStorage {
    void upload(ObjectUploadRequest uploadRequest);
}
