package org.roadmap.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface MinioStorage {
    void upload(String fileName, InputStream inputStream, long size, String contentType);
}
