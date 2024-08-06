package com.example.FileShareAPI.Back_End.dto;

public record FileDto(
        String fileId,
        String userId,
        String fileName
) {
}
