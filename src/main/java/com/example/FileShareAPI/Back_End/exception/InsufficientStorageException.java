package com.example.FileShareAPI.Back_End.exception;

public class InsufficientStorageException extends RuntimeException {
    public InsufficientStorageException(String message) {
        super(message);
    }
}
