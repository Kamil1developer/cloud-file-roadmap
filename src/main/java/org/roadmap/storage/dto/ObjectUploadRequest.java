package org.roadmap.storage.dto;

import java.io.InputStream;

public record ObjectUploadRequest(
        String fileName,
        InputStream inputStream,
        long size,
        String contentType
) {
}
