package com.example.FileShareAPI.Back_End.dto;

import java.time.LocalDateTime;

public record FileDescriptionDto (
        String fileId,
        String userId,
        String fileName,
        String sizeBytes,
        LocalDateTime timestamp,
        String fileExt,
        String description
        )
{ }
