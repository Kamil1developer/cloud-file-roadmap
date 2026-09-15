package org.roadmap.storage.exception;

public class ResourceAlreadyExistsException extends RuntimeException {

    public ResourceAlreadyExistsException() {
        super("файл уже существует");
    }
}
