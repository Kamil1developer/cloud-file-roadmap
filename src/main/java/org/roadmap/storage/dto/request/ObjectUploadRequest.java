package org.roadmap.storage.dto.request;

import java.io.InputStream;

public record ObjectUploadRequest(
        String fileName,
        InputStream inputStream,
        long size,
        String contentType
) {
}
