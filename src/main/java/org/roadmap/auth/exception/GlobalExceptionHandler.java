package org.roadmap.auth.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(){
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("400", "ошибка валидации"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateException(){
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("409", "пользователь занят"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(){
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("500", "неизвестная ошибка"));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(){
        return ResponseEntity.
                status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("401","неверные данные (такого пользователя нет, или пароль неправильный)"));
    }

}
