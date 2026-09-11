package org.roadmap.service;
import lombok.RequiredArgsConstructor;
import org.roadmap.dto.response.UploadResponse;
import org.roadmap.mapper.ResourceMapper;
import org.roadmap.storage.MinioStorage;
import org.roadmap.storage.dto.request.ObjectUploadRequest;
import org.roadmap.storage.model.StorageResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UploadResourceService {
    private final MinioStorage storage;
    private final ResourceMapper mapper;

    public Optional<List<UploadResponse>> upload(String path, List<MultipartFile> files) throws IOException {
        if (files.size() == 1) {
            MultipartFile file = files.getFirst();
            ObjectUploadRequest uploadRequest = new ObjectUploadRequest(
                    path + file.getOriginalFilename(),
                    file.getInputStream(),
                    file.getSize(),
                    file.getContentType()
            );

            StorageResource storageResource = storage.upload(uploadRequest);

            UploadResponse uploadResponse = mapper.toUploadResponse(storageResource);



            return Optional.of(List.of(uploadResponse));
        }
        if (files.size() > 1) {
            List<UploadResponse> uploadResponses = new ArrayList<>();
            for (MultipartFile file : files) {

                ObjectUploadRequest uploadRequest = new ObjectUploadRequest(
                        path + file.getOriginalFilename(),
                        file.getInputStream(),
                        file.getSize(),
                        file.getContentType()
                );
                String type = file.getContentType();
                StorageResource storageResources = storage.upload(uploadRequest);
                UploadResponse uploadResponse = mapper.toUploadResponse(storageResources);

                uploadResponses.add(uploadResponse);
            }
            return Optional.of(uploadResponses);

        }
        return Optional.empty();
    }
}
