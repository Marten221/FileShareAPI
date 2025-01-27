package com.example.FileShareAPI.Back_End.dto;

public record DiskSpaceDto(
        long usedSpace,
        long totalSpace
) {
}
