package com.example.FileShareAPI.Back_End.controller;

import com.example.FileShareAPI.Back_End.dto.FileDto;
import com.example.FileShareAPI.Back_End.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @GetMapping("/test")
    public void test(@RequestParam("token")String token) {
        //System.out.println(JwtUtil.validateToken((token)));
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @PostMapping("/file")
    public ResponseEntity<FileDto> createFile(@RequestParam("file") MultipartFile file,
                                              @RequestParam(value = "customFilename", required = false) String customFilename,
                                              @RequestParam(value = "description", required = false) String desc,
                                              @RequestParam(value = "isPublic", required = false) boolean isPublic)

            throws IOException {
        return ResponseEntity.ok().body(fileService.createFile(file, customFilename, desc, isPublic));
    }

    @PutMapping("/file")
    public ResponseEntity<FileDto> updateFile(@RequestParam("fileId") String fileId,
                                              @RequestParam(value = "file", required = false) MultipartFile file,
                                              @RequestParam(value = "customFilename", required = false) String customFilename,
                                              @RequestParam(value = "description", required = false) String desc,
                                              @RequestParam(value = "isPublic", required = false, defaultValue = "false") Boolean isPublic)
            throws IOException {
        return ResponseEntity.ok().body(fileService.updateFile(fileId, file, customFilename, desc, isPublic));
    }

    @GetMapping(value = "/public/download/{fileId}", produces = MediaType.ALL_VALUE)
    public ResponseEntity<byte[]> getFile(@PathVariable("fileId") String fileId)
            throws IOException {
        byte[] fileContent = fileService.getFileContent(fileId);
        HttpHeaders header = fileService.createHeader(fileId);

        return new ResponseEntity<>(fileContent, header, HttpStatus.OK);
    }

    @GetMapping("/public/findfile/{keyword}")
    public Page<FileDto> getByKeyword(@PathVariable(value = "keyword", required = false) String keyword,
                                      @RequestParam(value = "sorting", defaultValue = "name_ascending") String sorting,
                                      @RequestParam(value = "extension", defaultValue = "any") String extension,
                                      @RequestParam(value = "page", defaultValue = "0") int page,
                                      @RequestParam(value = "size", defaultValue = "15") int size) {
        return fileService.getFilesByKeyword(keyword, sorting, extension, page, size);
    }

    @GetMapping("/filedescription/{fileId}")
    public ResponseEntity<FileDto> getFileDescription(@PathVariable("fileId") String fileId) { //FileDescriptionDto instead of FileDto
        return ResponseEntity.ok().body(fileService.getFileDescription(fileId));
    }

    //TODO: periodically check, if the database and file_share folder have the sama data about files. If some files have been deleted, delete them from the db aswell.
}
