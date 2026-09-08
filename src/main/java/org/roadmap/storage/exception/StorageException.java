package org.roadmap.storage.exception;

import lombok.Getter;

@Getter
public class StorageException extends RuntimeException {
    private final String message = "неизвестная ошибка";

    public StorageException(){
        super("неизвестная ошибка");
    }

}
