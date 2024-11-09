package com.example.FileShareAPI.Back_End.service;

import com.example.FileShareAPI.Back_End.dto.FileDto;
import com.example.FileShareAPI.Back_End.model.File;
import com.example.FileShareAPI.Back_End.model.User;
import com.example.FileShareAPI.Back_End.repo.FileRepo;
import com.example.FileShareAPI.Back_End.repo.UserRepo;
import com.example.FileShareAPI.Back_End.specification.FileSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
import static utils.FileUtils.*;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepo fileRepo;
    private final UserRepo userRepo;



    public FileDto createFile(String id,
                              MultipartFile file,
                              String customFilename,
                              String description,
                              Boolean isPublic) throws IOException {
        assert customFilename != null;
        assert description != null;
        customFilename = truncateString(customFilename, 255); //truncate method also checks for null
        description = truncateString(description, 255);

        User fileOwner = userRepo.getReferenceById(id);
        String originalFilename = file.getOriginalFilename();

        assert originalFilename != null;
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        long fileSize = file.getSize();

        File fileObject = new File(fileOwner,
                extension,
                originalFilename,
                fileSize,
                bytesToHumanReadable(fileSize),
                customFilename,
                description,
                isPublic);// Creating the file object

        fileRepo.save(fileObject); // saving the file to the database and file system
        String newFileName = fileObject.getFileId() + "." + extension;
        saveFile(newFileName, file, id);

        return fileObject.toDto(false);
    }

    public byte[] getFileContent(String fileId) throws IOException { //TODO: check if the user has access to this file!
        return Files.readAllBytes(getFilePath(fileId)); //TODO: rename file before sending it
    }

    public HttpHeaders createHeader(String fileId) throws IOException {
        String contentType = Files.probeContentType(getFilePath(fileId));
        if (contentType == null) contentType = "application/octet-stream";// Default to binary stream if type is unknown

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.parseMediaType(contentType));
        header.setContentDispositionFormData("attachment", fileId);

        return header;
    }

    private Path getFilePath(String fileId) {
        File file = fileRepo.getReferenceById(fileId);
        String fileLocation = FILE_DIRECTORY + file.getUser().getUserId() + "/" + file.getFileId() + "." + file.getFileExtension();
        return Paths.get(fileLocation);
    }

    public Page<FileDto> getFilesByKeyword(String keyword,
                                           String sorting,
                                           String extension,
                                           String userId,
                                           int page,
                                           int size) {
        System.out.println("keyword" + keyword);

        page = Math.max(0, page); //Min page nr 0
        size = Math.min(size, 30); //Max page size 30
        Sort sortingMethod = findSorting(sorting);

        Pageable pageable = PageRequest.of(page, size, sortingMethod);

        Specification<File> spec = Specification.where(FileSpecifications.hasKeyword(keyword))
                .and(FileSpecifications.isAccessible(userId))
                .and(FileSpecifications.hasExtension(extension));

        Page<File> filesPage = fileRepo.findAll(spec, pageable);
        return filesPage.map(file -> file.toDto(true));
    }


    public FileDto getFileDescription(String fileId) {
        return fileRepo.getReferenceById(fileId).toDto(false);
    }


    public FileDto updateFile(String userId,
                              String fileId,
                              MultipartFile newFile,
                              String customFilename,
                              String desc,
                              boolean isPublic) throws IOException {
        File fileToUpdate = verifyFileOwnership(userId, fileId);

        if (!newFile.isEmpty()) {
            deleteFile(userId, fileId); //If a new file was uploaded, delete the previous file.

            String originalFilename = newFile.getOriginalFilename();
            assert originalFilename != null;
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            fileToUpdate.setFileExtension(extension);

            saveFile(fileToUpdate.getFileId() + "." + extension, newFile, userId);

            if (stringIsNullorBlank(customFilename)){ //If no custom name was specified, use the files original name
                originalFilename = originalFilename.substring(0, originalFilename.lastIndexOf(".")); //TODO: if the file has no name/extension, an error occurs // Move to File class
                fileToUpdate.setFileName(originalFilename);
            }


            long fileSize = newFile.getSize();
            fileToUpdate.setSizeBytes(fileSize);
            fileToUpdate.setSizeHumanReadable(bytesToHumanReadable(fileSize));
        }

        if (!stringIsNullorBlank(customFilename)) fileToUpdate.setFileName(customFilename);
        if (!stringIsNullorBlank(desc)) fileToUpdate.setDescription(desc);
        fileToUpdate.setIsPublic(isPublic);


        fileToUpdate.setTimestamp(LocalDateTime.now());
        //TODO: create another db table for logging operations made with the file
        File updatedFile = fileRepo.save(fileToUpdate);

        return updatedFile.toDto(false);
    }

    public void deleteFile(String userId, String fileId) throws IOException {
        File fileToDelete = verifyFileOwnership(userId, fileId);
        //TODO: create a class method for finding filestoragelocation.

        String customFile_Directory = FILE_DIRECTORY + userId + "/" + fileId + "." + fileToDelete.getFileExtension(); // Custom folder for each user
        Path fileStorageLocation = Paths.get(customFile_Directory).toAbsolutePath().normalize();

        Files.delete(fileStorageLocation);
    }



    private File verifyFileOwnership(String userId, String fileId) {
        User fileOwner = userRepo.getReferenceById(userId);
        File fileToUpdate = fileRepo.findByUserAndFileId(fileOwner, fileId);
        assert fileToUpdate != null; //TODO: remove for production
        // If fileToUpdate is null, then the user is not allowed to manipulate the file/ the file doesnt exist.
        return fileToUpdate;
    }

}
