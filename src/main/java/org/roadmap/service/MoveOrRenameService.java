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

    public MoveOrRenameResponse moveOrRename(String from, String to){
        if(Path.of(from).getFileName().equals(Path.of(to).getFileName())) {
            if (from.endsWith("/")) {
                List<StorageResource> resources = storage.getContentByDirectory(from);
                for (StorageResource resource : resources) {

                    String resourcePathFrom = resource.path() + resource.name();
                    String resourcePathTo = to +  resource.name();
                    storage.moveObject(resourcePathFrom, resourcePathTo);
                }
                String parentPath = Path.of(to).getParent().toString() + "/";
                String directoryName = Path.of(to).getFileName().toString();
                return new MoveOrRenameResponse(parentPath, directoryName, null, "DIRECTORY");
            }
            else {
                StorageResource resource = storage.getResourceByPath(from);
                String resourcePathFrom = resource.path() + resource.name();
                String resourcePathTo = to;
                storage.renameObject(resourcePathFrom, resourcePathTo);

                String parentPath = Path.of(to).getParent().toString() + "/";
                String fileName = Path.of(to).getFileName().toString();

                return new MoveOrRenameResponse(parentPath, fileName, resource.size(), "FILE");
            }
        }
        else{
            return null;
        }

    }
}
