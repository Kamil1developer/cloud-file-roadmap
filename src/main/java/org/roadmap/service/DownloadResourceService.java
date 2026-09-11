package org.roadmap.service;

import lombok.RequiredArgsConstructor;
import org.roadmap.storage.MinioStorage;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class DownloadResourceService {
    private final MinioStorage storage;

    public InputStream downloadResource(String path){
        return storage.downloadResourceByPath(path);
    }
}
