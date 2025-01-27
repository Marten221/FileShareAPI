package com.example.FileShareAPI.Back_End.exception;

import org.springframework.http.HttpStatus;

public class RegistrationCodeInvalidException extends RuntimeException {
    public HttpStatus status;
    public RegistrationCodeInvalidException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
