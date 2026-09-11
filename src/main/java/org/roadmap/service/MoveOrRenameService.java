package org.roadmap.service;

import lombok.RequiredArgsConstructor;
import org.roadmap.dto.response.MoveOrRenameResponse;
import org.roadmap.mapper.ResourceMapper;
import org.roadmap.storage.MinioStorage;
import org.roadmap.storage.model.StorageResource;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MoveOrRenameService {
    private final MinioStorage storage;
    private final ResourceMapper mapper;

    public MoveOrRenameResponse moveOrRename(String from, String to) {
        if (from.endsWith("/")) {

            List<StorageResource> resources = storage.findAllResourcesByPrefix(from);

            for (StorageResource resource : resources) {

                String resourcePathFrom = resource.path() + resource.name();

                if ("DIRECTORY".equals(resource.type())
                        && !resourcePathFrom.endsWith("/")) {
                    resourcePathFrom += "/";
                }

                String relativePath = resourcePathFrom.substring(from.length());

                String resourcePathTo = to + relativePath;

                storage.moveOrRenameObject(
                        resourcePathFrom,
                        resourcePathTo
                );
            }

            String parentPath = Path.of(to).getParent() == null
                    ? ""
                    : Path.of(to).getParent().toString() + "/";

            String directoryName = Path.of(to).getFileName().toString();

            return new MoveOrRenameResponse(
                    parentPath,
                    directoryName,
                    null,
                    "DIRECTORY"
            );

        } else {

            StorageResource resource = storage.getResourceByPath(from);

            String resourcePathFrom =
                    resource.path() + resource.name();

            String resourcePathTo = to;

            storage.moveOrRenameObject(
                    resourcePathFrom,
                    resourcePathTo
            );

            String parentPath = Path.of(to).getParent() == null
                    ? ""
                    : Path.of(to).getParent().toString() + "/";

            String fileName =
                    Path.of(to).getFileName().toString();

            return new MoveOrRenameResponse(
                    parentPath,
                    fileName,
                    resource.size(),
                    "FILE"
            );
        }
    }
}
