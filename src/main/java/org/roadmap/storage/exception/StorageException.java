package org.roadmap.storage.exception;

import lombok.Getter;

@Getter
public class StorageException extends RuntimeException {
    public StorageException(){
        super("неизвестная ошибка");
    }

}
