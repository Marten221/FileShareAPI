package com.example.FileShareAPI.Back_End.service;

import com.example.FileShareAPI.Back_End.dto.FilePreviewDto;
import com.example.FileShareAPI.Back_End.model.File;
import com.example.FileShareAPI.Back_End.model.User;
import com.example.FileShareAPI.Back_End.repo.FileRepo;
import com.example.FileShareAPI.Back_End.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static com.example.FileShareAPI.Back_End.constant.Constant.FILE_DIRECTORY;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepo fileRepo;
    private final UserRepo userRepo;

    public FilePreviewDto createFile(String id, MultipartFile file, String customFilename, String description) throws IOException {
        User fileOwner = userRepo.getReferenceById(id);
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null; //TODO: remove for production
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

        File fileObject = createFileObject(fileOwner, extension, originalFilename, customFilename, description, LocalDateTime.now());// Creating the file and setting its values

        fileRepo.save(fileObject); // saving the file to the database and file system
        String newFileName = fileObject.getFileId() + "." + extension;
        saveFile(newFileName, file, id);

        return fileToPreviewDto(fileObject);
    }

    private static File createFileObject(User fileOwner, String extension, String originalFilename, String customFileName, String desc, LocalDateTime timestamp) {
        File fileObject = new File();
        fileObject.setUser(fileOwner);
        fileObject.setFileExtension(extension);
        fileObject.setFileName(originalFilename); //TODO: remove file ext from the original filename
        fileObject.setFileName(customFileName == null ? originalFilename : customFileName); // If no custom name is given, use the original file Name
        fileObject.setDescription(desc);
        fileObject.setTimestamp(timestamp);

        return fileObject;
    }


    private void saveFile(String fileName, MultipartFile file, String userId) throws IOException {
        String customFile_Directory = FILE_DIRECTORY + userId + "/"; // Custom folder for each user
        Path fileStorageLocation = Paths.get(customFile_Directory).toAbsolutePath().normalize();

        if (!Files.exists(fileStorageLocation)) {
            Files.createDirectories(fileStorageLocation);
        } // If there is no such directory, create it

        Files.copy(file.getInputStream(), fileStorageLocation.resolve(fileName), REPLACE_EXISTING);
    }

    private FilePreviewDto fileToPreviewDto(File fileObject) {
        return new FilePreviewDto(fileObject.getFileId(), fileObject.getUser().getUserId(), fileObject.getFileName(), fileObject.getTimestamp());
    }

    public byte[] getFileContent(String fileName) throws IOException {
        return Files.readAllBytes(getFilePath(fileName)); //TODO: rename file before sending it
    }

    public HttpHeaders createHeader(String fileName) throws IOException {
        String contentType = Files.probeContentType(getFilePath(fileName));
        if (contentType == null) contentType = "application/octet-stream";// Default to binary stream if type is unknown

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.parseMediaType(contentType));
        header.setContentDispositionFormData("attachment", fileName);

        return header;
    }

    private Path getFilePath(String fileName) {
        File file = fileRepo.getReferenceById(fileName);
        String fileLocation = FILE_DIRECTORY + file.getUser().getUserId() + "/" + file.getFileId() + "." + file.getFileExtension();
        return Paths.get(fileLocation);
    }

    public Page<FilePreviewDto> getFilesByKeyword(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp"));
        Page<File> filesPage = fileRepo.findByKeyword(keyword, pageable);
        return filesPage.map(file -> fileToPreviewDto(file));
    }

    public FilePreviewDto getFileDescription(String fileId) {
        return fileToPreviewDto(fileRepo.getReferenceById(fileId)); //TODO: FileDescriptionDto
    }
}
