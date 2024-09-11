package com.example.FileShareAPI.Back_End.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FileDescriptionDto extends FilePreviewDto{
        String sizeBytes;
        String description;

        public FileDescriptionDto(String fileId, String userId, String fileName, String fileExt, LocalDateTime timestamp, String sizeBytes, String description) {
                super(fileId, userId, fileName, fileExt, timestamp);
                this.sizeBytes = sizeBytes;
                this.description = description;
        }
}
