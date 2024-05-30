package com.artShop.artShop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<StackTraceElement[]> handleEntityNotFoundException(EntityNotFoundException enfe) {
        return new ResponseEntity<>(enfe.getStackTrace(), HttpStatus.NOT_FOUND);
    }
}
