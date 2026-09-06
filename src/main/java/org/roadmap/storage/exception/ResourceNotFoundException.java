package org.roadmap.storage.exception;


import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private static final String message = "Ресурс не найден";

    public ResourceNotFoundException() {
        super("Ресурс не найден");
    }

}
