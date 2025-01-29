package com.example.FileShareAPI.Back_End.dto;

public record DiskSpaceDto(
        long usedMemory,
        long totalMemory,
        String usedMemoryHumanReadable,
        String totalMemoryHumanReadable
) {
}
