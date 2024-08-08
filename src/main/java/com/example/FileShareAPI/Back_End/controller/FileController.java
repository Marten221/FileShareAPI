package com.example.FileShareAPI.Back_End.controller;

import com.example.FileShareAPI.Back_End.dto.FilePreviewDto;
import com.example.FileShareAPI.Back_End.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class FileController {
    @Autowired
    private final FileService fileService;

    @PostMapping("/file")
    public ResponseEntity<FilePreviewDto> createFile(@RequestParam("userId") String id,
                                                     @RequestParam("file") MultipartFile file,
                                                     @RequestParam(value = "customFilename", required = false) String customFilename,
                                                     @RequestParam(value = "description", required = false) String desc) //Give the file a custom name. This is the name displayed on site
            throws IOException {
        return ResponseEntity.ok().body(fileService.createFile(id, file, customFilename, desc));
    }

    @GetMapping(value = "/file/{fileName}", produces = MediaType.ALL_VALUE)
    public ResponseEntity<byte[]> getFile(@PathVariable("fileName") String fileName) throws IOException {
        byte[] fileContent = fileService.getFileContent(fileName);
        HttpHeaders header = fileService.createHeader(fileName);

        return new ResponseEntity<>(fileContent, header, HttpStatus.OK);
    }

    @GetMapping("/findfile/{keyword}") //TODO: pagination
    public Page<FilePreviewDto> getByKeyword(@PathVariable("keyword") String keyword,
                                             @RequestParam(value = "page", defaultValue = "0") int page,
                                             @RequestParam(value = "size", defaultValue = "15") int size) {
        return fileService.getFilesByKeyword(keyword, page, size);
    }

    @GetMapping("/filedescription/{fileId}")
    public ResponseEntity<FilePreviewDto> getFileDescription(@PathVariable("fileId") String fileId) { //FileDescriptionDto instead of FileDto
        return ResponseEntity.ok().body(fileService.getFileDescription(fileId));
    }

    //TODO: periodically check, if the database and file_share folder have the sama data about files. If some files have been deleted, delete them from the db aswell.
}
