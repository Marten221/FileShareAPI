package com.example.FileShareAPI.Back_End.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class FilePreviewDto {
    String fileId;
    String userId;
    String fileName;
    String fileExt;
    Map<String, String> timestamp; // tee ylemklass ja alamklass, saada aeg lahtivõetuna

    public FilePreviewDto(String fileId, String userId, String fileName, String fileExt, LocalDateTime timestamp) {
        this.fileId = fileId;
        this.userId = userId;
        this.fileName = fileName;
        this.fileExt = fileExt;

        Map<String, String> timeMap = new HashMap<>();
        timeMap.put("year", String.valueOf(timestamp.getYear()));
        timeMap.put("month", addLeadingZero(timestamp.getMonthValue()));
        timeMap.put("day", addLeadingZero(timestamp.getDayOfMonth()));
        timeMap.put("hour", addLeadingZero(timestamp.getHour()));
        timeMap.put("minute", addLeadingZero(timestamp.getMinute()));
        this.timestamp = timeMap;
    }

    private String addLeadingZero(int value){
        return (value < 10) ? "0" + value : String.valueOf(value);
    }
}
