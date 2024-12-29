package com.example.FileShareAPI.Back_End.exception;


public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
