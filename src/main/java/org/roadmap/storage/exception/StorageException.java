package org.roadmap.storage.exception;

public class StorageException extends RuntimeException {
    public StorageException(Throwable cause) {

        super("Ошибка с хранилищем");
    }
}
