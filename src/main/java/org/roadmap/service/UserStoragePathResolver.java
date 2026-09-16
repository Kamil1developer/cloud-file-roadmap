package org.roadmap.service;

import org.roadmap.storage.model.DirectoryResource;
import org.roadmap.storage.model.StorageResource;
import org.springframework.stereotype.Component;

@Component
public class UserStoragePathResolver {

    public String userRoot(String username) {
        return "user-" + username + "-files/";
    }

    public String toStoragePath(String username, String publicPath) {
        return userRoot(username) + publicPath;
    }

    public String toPublicPath(String username, String storagePath) {
        String userRoot = userRoot(username);
        String userRootWithoutSlash = userRoot.substring(0, userRoot.length() - 1);

        if (storagePath.equals(userRootWithoutSlash)) {
            return "";
        }

        if (!storagePath.startsWith(userRoot)) {
            throw new IllegalStateException("Ресурс не принадлежит пользователю " + username);
        }

        return storagePath.substring(userRoot.length());
    }

    public StorageResource toPublicResource(String username, StorageResource resource) {
        return new StorageResource(
                toPublicPath(username, resource.path()),
                resource.name(),
                resource.size(),
                resource.type()
        );
    }

    public DirectoryResource toPublicResource(String username, DirectoryResource resource) {
        String publicPath = toPublicPath(username, resource.path());

        if (!publicPath.isEmpty() && !publicPath.endsWith("/")) {
            publicPath += "/";
        }

        return new DirectoryResource(
                publicPath,
                resource.name(),
                resource.type()
        );
    }
}
