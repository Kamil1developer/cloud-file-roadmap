package org.roadmap.storage.exception;


import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {
        super("Ресурс не найден");
    }

}
