package org.roadmap.storage;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.DeleteRequest;
import io.minio.messages.DeleteResult;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.roadmap.config.storage.MinioProperties;
import org.roadmap.mapper.DirectoryContentMapper;
import org.roadmap.storage.dto.request.ObjectUploadRequest;
import org.roadmap.storage.exception.ResourceNotFoundException;
import org.roadmap.storage.exception.StorageException;
import org.roadmap.storage.model.DirectoryResource;
import org.roadmap.storage.model.StorageResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MinioStorageImpl implements MinioStorage {
    private final MinioClient client;
    private final MinioProperties properties;

    @Override
    public StorageResource upload(ObjectUploadRequest uploadRequest) {
        String bucketName = properties.getBucket();
        try {

            if (client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                client.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .stream(uploadRequest.inputStream(), uploadRequest.size(), (long) -1)
                        .contentType(uploadRequest.contentType())
                        .object(uploadRequest.fileName())
                        .build());

            }
            else {
                client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());

                client.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .stream(uploadRequest.inputStream(), uploadRequest.size(), (long) -1)
                        .contentType(uploadRequest.contentType())
                        .object(uploadRequest.fileName())
                        .build());
            }

            Iterable<Result<Item>> objects = client.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(properties.getBucket())
                            .prefix(uploadRequest.fileName())
                            .build()
            );
            return toStorageResource(objects);
        } catch (MinioException e) {
            throw new StorageException();
        }
    }

    public StorageResource toStorageResource(Iterable<Result<Item>> objects) {
        try {
            for (Result<Item> item : objects) {

                String parentPath = Path.of(item.get().objectName()).getParent() != null ?
                        Path.of(item.get().objectName()).getParent().toString(): "";
                parentPath = parentPath + "/";

                String fileName = Path.of(item.get().objectName()).getFileName().toString();
                long size = item.get().size();
                String type = item.get().isDir() ? "DIRECTORY" : "FILE";

                return new StorageResource(parentPath, fileName, size, type);
            }

            throw new StorageException();
        }
        catch (MinioException e) {
            throw new StorageException();
        }
    }

    public List<StorageResource> getContentByDirectory(String path) {
        try {
            if (path.isEmpty()) {
                Iterable<Result<Item>> objects = client.listObjects(ListObjectsArgs.builder()
                        .bucket(properties.getBucket())
                        .build()
                );

                return toStorageResources(objects);
            }
            else {
                Iterable<Result<Item>> objects = client.listObjects(ListObjectsArgs.builder()
                        .bucket(properties.getBucket())
                        .prefix(path)
                        .build()
                );
                return toStorageResources(objects);
            }

        } catch (MinioException e) {
            throw new StorageException();
        }
    }

    @Override
    public void deleteByPath(String path) {
        try {
            Iterable<Result<Item>> objects = client.listObjects(ListObjectsArgs.builder()
                    .bucket(properties.getBucket())
                    .prefix(path)
                    .build()
            );
            List<DeleteRequest.Object> deleteObjects = new ArrayList<>();
            for (Result<Item> item: objects) {
                deleteObjects.add(new DeleteRequest.Object(item.get().objectName()));
            }

            Iterable<Result<DeleteResult.Error>> results =
                    client.removeObjects(
                            RemoveObjectsArgs.builder()
                                    .bucket(properties.getBucket())
                                    .objects(deleteObjects)
                                    .build()
                    );
            if (deleteObjects.isEmpty()){
                throw new ResourceNotFoundException();
            }
            for (Result<DeleteResult.Error> errorResult: results){
                DeleteResult.Error error = errorResult.get();
            }
        }
        catch (MinioException e){
            throw new StorageException();
        }
    }

    private List<StorageResource> toStorageResources(Iterable<Result<Item>> objects) throws MinioException {

        List<StorageResource> resources = new ArrayList<>();
        for (Result<Item> item : objects) {

            String parentPath = Path.of(item.get().objectName()).getParent() != null ?
                    Path.of(item.get().objectName()).getParent().toString(): "";
            parentPath = parentPath + "/";
            String fileName = Path.of(item.get().objectName()).getFileName().toString();
            long size = item.get().size();
            String type = item.get().objectName().endsWith("/") ? "DIRECTORY" : "FILE";

            resources.add(new StorageResource(parentPath, fileName, size, type));

        }
        return resources;
    }

    public StorageResource getResourceByPath(String path){
        try {
            StatObjectResponse response = client.statObject(StatObjectArgs.builder()
                    .bucket(properties.getBucket())
                    .object(path)
                    .build()
            );

            return new StorageResource(
                    Path.of(path).getParent().toString() + "/",
                    Path.of(path).getFileName().toString(),
                    response.size(),
                    response.contentType()
                    );
        }

        catch (MinioException e){
            throw new StorageException();
        }
    }

    @Override
    public DirectoryResource createDirectoryByPath(String path) {
        String bucketName = properties.getBucket();

        try {

            if (client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                client.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .stream(InputStream.nullInputStream(), 0L, -1L)
                        .object(path)
                        .build());

            }
            else {
                client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());

                client.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .stream(InputStream.nullInputStream(), 0L, -1L)
                        .object(path)
                        .build());
            }

            StatObjectResponse response = client.statObject(StatObjectArgs.builder()
                    .bucket(properties.getBucket())
                    .object(path)
                    .build()
            );

            String type = response.object().endsWith("/") ? "DIRECTORY" : "FILE";

            return new DirectoryResource(
                    Path.of(path).getParent().toString(),
                    Path.of(path).getFileName().toString(),
                    type
                    );

        } catch (MinioException e) {
            throw new StorageException();
        }

    }
}
