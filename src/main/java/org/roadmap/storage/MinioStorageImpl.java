package org.roadmap.storage;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.DeleteRequest;
import io.minio.messages.DeleteResult;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.roadmap.config.storage.MinioProperties;
import org.roadmap.storage.dto.request.ObjectUploadRequest;
import org.roadmap.storage.exception.ResourceNotFoundException;
import org.roadmap.storage.exception.StorageException;
import org.roadmap.storage.mapper.MinioStorageMapper;
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
    private final MinioStorageMapper storageMapper;


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

            } else {
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
            return storageMapper.toStorageResource(objects);
        } catch (MinioException e) {
            throw new StorageException();
        }
    }


    public List<StorageResource> getContentByDirectory(String path) {
        try {
            Iterable<Result<Item>> objects = client.listObjects(ListObjectsArgs.builder()
                    .bucket(properties.getBucket())
                    .prefix(path)
                    .build()
            );

            return storageMapper.toStorageResources(objects, path);

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
            for (Result<Item> item : objects) {
                Item minioItem = item.get();
                deleteObjects.add(new DeleteRequest.Object(minioItem.objectName()));
            }

            Iterable<Result<DeleteResult.Error>> results =
                    client.removeObjects(
                            RemoveObjectsArgs.builder()
                                    .bucket(properties.getBucket())
                                    .objects(deleteObjects)
                                    .build()
                    );
            if (deleteObjects.isEmpty()) {
                throw new ResourceNotFoundException();
            }
            for (Result<DeleteResult.Error> errorResult : results) {
                DeleteResult.Error error = errorResult.get();
            }
        } catch (MinioException e) {
            throw new StorageException();
        }
    }

    public StorageResource getResourceByPath(String path) {
        try {
            StatObjectResponse response = client.statObject(StatObjectArgs.builder()
                    .bucket(properties.getBucket())
                    .object(path)
                    .build()
            );
            Path parent = Path.of(path).getParent();

            String pathParent = parent == null ? "" : parent.toString() + "/";
            String name = Path.of(path).getFileName().toString();
            Long size = !(response.size() == 0) ? response.size(): null;
            String type = response.object().endsWith("/") ? "DIRECTORY" : "FILE";

            return new StorageResource(pathParent, name, size, type);
        } catch (MinioException e) {
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

            } else {
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

            Path parent = Path.of(path).getParent();

            String pathParent = parent == null ? "" : parent.toString();
            String name = Path.of(path).getFileName().toString();
            String type = response.object().endsWith("/") ? "DIRECTORY" : "FILE";

            return new DirectoryResource(pathParent, name, type);

        } catch (MinioException e) {
            throw new StorageException();
        }

    }

    @Override
    public StorageResource renameObject(String from, String to) {
        String bucketName = properties.getBucket();

        try {
            client.copyObject(CopyObjectArgs.builder()
                    .bucket(bucketName)
                    .object(to)
                    .source(SourceObject.builder()
                            .bucket(bucketName)
                            .object(from)
                            .build())
                    .build());

            deleteByPath(from);

            StatObjectResponse response = client.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(to)
                            .build()
            );
            String path = response.object();
            Path parent = Path.of(path).getParent();

            String pathParent = parent == null ? "" : parent.toString();
            String name = Path.of(path).getFileName().toString();


            Long size = !(response.size() == 0) ? response.size(): null;
            String type = response.object().endsWith("/") ? "DIRECTORY" : "FILE";


            return new StorageResource(pathParent, name, size, type);

        } catch (MinioException e) {
            throw new StorageException();
        }
    }

    @Override
    public StorageResource moveObject(String from, String to) {
        String bucketName = properties.getBucket();

        try {
            client.copyObject(CopyObjectArgs.builder()
                    .bucket(bucketName)
                    .object(to)
                    .source(SourceObject.builder()
                            .bucket(bucketName)
                            .object(from)
                            .build())
                    .build());

            deleteByPath(from);

            StatObjectResponse response = client.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(to)
                            .build()
            );
            String path = response.object();
            Path parent = Path.of(path).getParent();

            String pathParent = parent == null ? "" : parent.toString();
            String name = Path.of(path).getFileName().toString();


            Long size = !(response.size() == 0) ? response.size(): null;
            String type = response.object().endsWith("/") ? "DIRECTORY" : "FILE";


            return new StorageResource(pathParent, name, size, type);

        } catch (MinioException e) {
            throw new StorageException();
        }
    }

    @Override
    public InputStream downloadResourceByPath(String path) {
        try {
            return client.getObject(GetObjectArgs.builder()
                    .bucket(properties.getBucket())
                    .object(path)
                    .build());

        }
        catch (MinioException e){
            throw new StorageException();
        }
    }

    @Override
    public List<StorageResource> findResourcesByName(String query) {
        try {

            Iterable<Result<Item>> objects = client.listObjects(ListObjectsArgs.builder()
                    .bucket(properties.getBucket())
                    .recursive(true)
                    .build());


            List<StorageResource> resourcesByQuery = new ArrayList<>();

            for (Result<Item> item : objects) {
                Item minioItem = item.get();
                Path pathItem = Path.of(minioItem.objectName());
                String parentPath = pathItem.getParent() != null ?
                        pathItem.getParent().toString() + "/" : "";
                String fileName = pathItem.getFileName() != null ?
                        pathItem.getFileName().toString() :
                        "";
                Long size = !(minioItem.size() == 0) ? minioItem.size(): null;
                String type = minioItem.objectName().endsWith("/") ? "DIRECTORY" : "FILE";

                if (fileName.toLowerCase().contains(query.toLowerCase())) {
                    resourcesByQuery.add(new StorageResource(parentPath, fileName, size, type));
                }
            }
            return resourcesByQuery;
        } catch (MinioException e) {
            throw new StorageException();
        }
    }
}
