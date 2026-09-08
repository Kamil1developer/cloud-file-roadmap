package org.roadmap.storage.model;

public record StorageResource(
        String path,
        String name,
        long size,
        String type
){
}
