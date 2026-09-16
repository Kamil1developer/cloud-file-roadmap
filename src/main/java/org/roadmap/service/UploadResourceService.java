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
    private final UserStoragePathResolver pathResolver;

    public Optional<List<UploadResponse>> upload(String username, String path, List<MultipartFile> files) throws IOException {
        if (files.size() == 1) {
            MultipartFile file = files.getFirst();
            ObjectUploadRequest uploadRequest = new ObjectUploadRequest(
                    pathResolver.toStoragePath(username, path + file.getOriginalFilename()),
                    file.getInputStream(),
                    file.getSize(),
                    file.getContentType()
            );

            StorageResource storageResource = pathResolver.toPublicResource(
                    username,
                    storage.upload(uploadRequest)
            );

            UploadResponse uploadResponse = mapper.toUploadResponse(storageResource);



            return Optional.of(List.of(uploadResponse));
        }
        if (files.size() > 1) {
            List<UploadResponse> uploadResponses = new ArrayList<>();
            for (MultipartFile file : files) {

                ObjectUploadRequest uploadRequest = new ObjectUploadRequest(
                        pathResolver.toStoragePath(username, path + file.getOriginalFilename()),
                        file.getInputStream(),
                        file.getSize(),
                        file.getContentType()
                );
                StorageResource storageResources = pathResolver.toPublicResource(
                        username,
                        storage.upload(uploadRequest)
                );
                UploadResponse uploadResponse = mapper.toUploadResponse(storageResources);

                uploadResponses.add(uploadResponse);
            }
            return Optional.of(uploadResponses);

        }
        return Optional.empty();
    }
}
