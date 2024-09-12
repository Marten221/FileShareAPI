package com.example.FileShareAPI.Back_End.controller;

import com.example.FileShareAPI.Back_End.dto.FileDto;
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
import java.util.Set;
//TODO: http response codes
@RestController
@RequiredArgsConstructor
public class FileController {
    @Autowired
    private final FileService fileService;

    @PostMapping("/file")
    public ResponseEntity<FileDto> createFile(@RequestParam("userId") String id,
                                              @RequestParam("file") MultipartFile file,
                                              @RequestParam(value = "customFilename", required = false) String customFilename,
                                              @RequestParam(value = "description", required = false) String desc,
                                              @RequestParam(value = "isPublic", required = false) boolean isPublic)

            throws IOException {
        return ResponseEntity.ok().body(fileService.createFile(id, file, customFilename, desc, isPublic));
    }

    @GetMapping(value = "/download/{fileId}", produces = MediaType.ALL_VALUE)
    public ResponseEntity<byte[]> getFile(@PathVariable("fileId") String fileId) throws IOException {
        byte[] fileContent = fileService.getFileContent(fileId);
        HttpHeaders header = fileService.createHeader(fileId);

        return new ResponseEntity<>(fileContent, header, HttpStatus.OK);
    }

    @GetMapping("/findfile/{keyword}")
    public Page<FileDto> getByKeyword(@PathVariable(value = "keyword", required = false) String keyword,
                                      @RequestParam(value = "sorting", defaultValue = "name_ascending") String sorting,
                                      @RequestParam(value = "extension") String extension,
                                      @RequestParam(value = "userId", required = false) String userId,
                                      @RequestParam(value = "page", defaultValue = "0") int page,
                                      @RequestParam(value = "size", defaultValue = "15") int size) {
        return fileService.getFilesByKeyword(keyword, sorting, extension, userId, page, size);
    }

    @GetMapping("/filedescription/{fileId}")
    public ResponseEntity<FileDto> getFileDescription(@PathVariable("fileId") String fileId) { //FileDescriptionDto instead of FileDto
        return ResponseEntity.ok().body(fileService.getFileDescription(fileId));
    }

    @GetMapping("/extensions")
    public ResponseEntity<Set<String>> getFileExtensions(@RequestParam(value = "userId") String userId) {
        return ResponseEntity.ok().body(fileService.getFileExtensions(userId));
    }
    //TODO: periodically check, if the database and file_share folder have the sama data about files. If some files have been deleted, delete them from the db aswell.
}
