package org.roadmap.exception;

import io.minio.errors.ErrorResponseException;
import org.roadmap.storage.exception.ResourceAlreadyExistsException;
import org.roadmap.storage.exception.ResourceNotFoundException;
import org.roadmap.storage.exception.StorageException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(){
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(400, "ошибка валидации"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateException(){
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(409, "пользователь занят"));
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyException(ResourceAlreadyExistsException exception){
        return ResponseEntity.
                status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(409, exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500, "неизвестная ошибка"));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(){
        return ResponseEntity.
                status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(401,"неверные данные (такого пользователя нет, или пароль неправильный)"));
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ErrorResponse> handleStorageException(StorageException exception){
        return ResponseEntity.
                status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500, exception.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException exception){
        return ResponseEntity.
                status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, exception.getMessage()));
    }
    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            HandlerMethodValidationException.class
    })
    public ResponseEntity<ErrorResponse> handleInvalidRequestParameter(){
        return ResponseEntity.
                status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400, "невалидный или отсутствующий путь"));
    }
}

