package org.roadmap.storage.mapper;

import io.minio.Result;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import lombok.NoArgsConstructor;
import org.roadmap.storage.exception.StorageException;
import org.roadmap.storage.model.StorageResource;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class MinioStorageMapper {


    public List<StorageResource> toStorageResourcesByDirectory(Iterable<Result<Item>> objects, String path) throws MinioException {

        List<StorageResource> resources = new ArrayList<>();

        for (Result<Item> item : objects) {
            Item minioItem = item.get();
            if(!path.equals(minioItem.objectName())) {

                Path pathItem = Path.of(minioItem.objectName());
                String parentPath = pathItem.getParent() != null ?
                        pathItem.getParent().toString() + "/" : "";
                String fileName = pathItem.getFileName() != null ?
                        pathItem.getFileName().toString() :
                        "";
                Long size = !(minioItem.size() == 0) ? minioItem.size() : null;
                String type = minioItem.objectName().endsWith("/") ? "DIRECTORY" : "FILE";

                resources.add(new StorageResource(parentPath, fileName, size, type));
            }


        }
        return resources;
    }

    public List<StorageResource> toStorageResourcesByPrefix(
            Iterable<Result<Item>> objects
    ) throws MinioException {

        List<StorageResource> resources = new ArrayList<>();

        for (Result<Item> item : objects) {

            Item minioItem = item.get();

            Path pathItem = Path.of(minioItem.objectName());

            String parentPath = pathItem.getParent() != null
                    ? pathItem.getParent().toString() + "/"
                    : "";

            String fileName = pathItem.getFileName() != null
                    ? pathItem.getFileName().toString()
                    : "";

            Long size = minioItem.size() != 0
                    ? minioItem.size()
                    : null;

            String type = minioItem.objectName().endsWith("/")
                    ? "DIRECTORY"
                    : "FILE";

            resources.add(
                    new StorageResource(
                            parentPath,
                            fileName,
                            size,
                            type
                    )
            );
        }

        return resources;
    }

    public StorageResource toStorageResource(Iterable<Result<Item>> objects) {
        try {
            for (Result<Item> item : objects) {
                Item minioItem = item.get();

                String parentPath = Path.of(minioItem.objectName()).getParent() != null ?
                        Path.of(minioItem.objectName()).getParent().toString() : "";
                parentPath = parentPath + "/";

                String fileName = Path.of(minioItem.objectName()).getFileName().toString();
                Long size = !(minioItem.size() == 0) ? minioItem.size(): null;
                String type = minioItem.isDir() ? "DIRECTORY" : "FILE";

                return new StorageResource(parentPath, fileName, size, type);
            }

            throw new StorageException();
        } catch (MinioException e) {
            throw new StorageException();
        }
    }
}
