package com.example.FileShareAPI.Back_End.dto;

import java.time.LocalDateTime;

public record FilePreviewDto(
        String fileId,
        String userId,
        String fileName,
        String fileExt,
        LocalDateTime timestamp
) {
}
