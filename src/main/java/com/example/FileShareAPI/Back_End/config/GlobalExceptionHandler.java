package com.example.FileShareAPI.Back_End.config;

import com.example.FileShareAPI.Back_End.exception.InvalidCredentialsException;
import com.example.FileShareAPI.Back_End.exception.ResourceNotFoundException;
import com.example.FileShareAPI.Back_End.exception.UnAuthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentialsException(InvalidCredentialsException e, WebRequest req) {
        return createErrorResponseEntity(
                HttpStatus.UNAUTHORIZED,
                e.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException e, WebRequest req) {
        return createErrorResponseEntity(
                HttpStatus.NOT_FOUND,
                e.getMessage());
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<?> handleUnAuthorizedException(Exception e, WebRequest req) {
        return createErrorResponseEntity(
                HttpStatus.UNAUTHORIZED,
                e.getMessage());
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleGlobalException(Exception e, WebRequest req) {
//        return createErrorResponseEntity(
//                HttpStatus.INTERNAL_SERVER_ERROR,
//                "An unexpected error occurred");
//    }

    public ResponseEntity<?> createErrorResponseEntity(HttpStatus httpStatus, String errorMessage) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", httpStatus.value());
        errorDetails.put("error", httpStatus.getReasonPhrase());
        errorDetails.put("message", errorMessage);
        return ResponseEntity.status(httpStatus).body(errorDetails);
    }

}
