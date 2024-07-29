package com.example.FileShareAPI.Back_End.controller;

import com.example.FileShareAPI.Back_End.dto.FileDto;
import com.example.FileShareAPI.Back_End.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FileController {
    @Autowired
    private final FileService fileService;

    @PostMapping("/file")
    public ResponseEntity<FileDto> createFile(@RequestParam("userId") String id,
                                              @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok().body(fileService.createFile(id, file));
    }

    @GetMapping(value = "/file/{fileName}", produces = MediaType.ALL_VALUE)
    public ResponseEntity<byte[]> getFile(@PathVariable("fileName") String fileName) throws IOException {
        byte[] fileContent = fileService.getFileContent(fileName);
        HttpHeaders header = fileService.createHeader(fileName);

        return new ResponseEntity<>(fileContent, header, HttpStatus.OK);
    }

    @GetMapping("/findfile/{keyword}") //TODO: pagination
    public List<FileDto> getByKeyword(@PathVariable("keyword") String keyword) {
        return fileService.getFilesByKeyword(keyword);
    }
}
