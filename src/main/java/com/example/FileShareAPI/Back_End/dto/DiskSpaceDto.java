package com.example.FileShareAPI.Back_End.dto;

public record DiskSpaceDto(
        long usableSpace,
        long totalSpace,
        long usedSpace
) {
}
