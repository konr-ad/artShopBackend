package com.artshop.backend.exception;

public class CodeNotFoundException extends EntityNotFoundException {
    public CodeNotFoundException(String message) {
        super(message);
    }
}
