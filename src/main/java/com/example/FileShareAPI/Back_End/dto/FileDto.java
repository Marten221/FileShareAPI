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
public class FileDto { // TODO: create a single file dto and another dto just for description and human readable? Possibly save human readable size into the db.
    String fileId;
    String fileName;
    String fileExt;
    String fileSize;
    String description;
    Map<String, String> timestamp;

    public FileDto(String fileId, String fileName, String fileExt, String fileSize, String description, LocalDateTime timestamp) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileExt = fileExt;
        this.fileSize = fileSize;
        this.description = description;

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
