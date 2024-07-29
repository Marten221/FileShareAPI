package com.example.FileShareAPI.Back_End.service;

import com.example.FileShareAPI.Back_End.dto.FileDto;
import com.example.FileShareAPI.Back_End.model.File;
import com.example.FileShareAPI.Back_End.model.User;
import com.example.FileShareAPI.Back_End.repo.FileRepo;
import com.example.FileShareAPI.Back_End.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.FileShareAPI.Back_End.constant.Constant.FILE_DIRECTORY;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepo fileRepo;
    private final UserRepo userRepo;

    public FileDto createFile(String id, MultipartFile file) throws IOException {
        User fileOwner = userRepo.getReferenceById(id);
        String fileName = file.getOriginalFilename();
        assert fileName != null; //TODO: remove for production
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

        File fileObject = new File(); // Creating the file and setting its values
        System.out.println(fileObject.getFileId());
        fileObject.setUser(fileOwner);
        fileObject.setFileExtension(extension);

        fileRepo.save(fileObject); // saving the file to the database and file system
        String newFileName = fileObject.getFileId() + "." + extension;
        saveFile(newFileName, file, id);

        return fileToDto(fileObject);
    }

    private void saveFile(String fileName, MultipartFile file, String userId) throws IOException {
        String customFile_Directory = FILE_DIRECTORY + userId + "/"; // Custom folder for each user
        Path fileStorageLocation = Paths.get(customFile_Directory).toAbsolutePath().normalize();

        if (!Files.exists(fileStorageLocation)) {
            Files.createDirectories(fileStorageLocation);
        } // If there is no such directory, create it

        Files.copy(file.getInputStream(), fileStorageLocation.resolve(fileName), REPLACE_EXISTING);
    }

    private FileDto fileToDto(File fileObject) {
        String fileName = fileObject.getFileId() + "." + fileObject.getFileExtension();

        return new FileDto(fileName, fileObject.getUser().getUserId());
    }

    public byte[] getFileContent(String fileName) throws IOException {
        return Files.readAllBytes(getFilePath(fileName));
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

    public List<FileDto> getFilesByKeyword(String keyword) {
        List<File> files = fileRepo.findByKeyword(keyword);
        return files.stream()
                .map(this::fileToDto)
                .collect(Collectors.toList());
    }

}
