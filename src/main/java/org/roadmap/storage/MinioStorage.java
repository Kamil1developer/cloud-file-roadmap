package org.roadmap.storage;

import io.minio.Result;
import io.minio.messages.DeleteResult;
import org.roadmap.storage.dto.request.ObjectUploadRequest;
import org.roadmap.storage.model.DirectoryResource;
import org.roadmap.storage.model.StorageResource;

import java.io.InputStream;
import java.util.List;

public interface MinioStorage {
    StorageResource  upload(ObjectUploadRequest uploadRequest);
    List<StorageResource> getContentByDirectory(String path);
    void deleteByPath(String path);
    void deleteByPrefix(String path);
    StorageResource getResourceByPath(String path);
    DirectoryResource createDirectoryByPath(String path);
    StorageResource moveOrRenameObject(String from, String to);
    InputStream downloadResourceByPath(String path);
    List<StorageResource> findResourcesByName(String query);
    List<StorageResource> findAllResourcesByPrefix(String prefix);



}
