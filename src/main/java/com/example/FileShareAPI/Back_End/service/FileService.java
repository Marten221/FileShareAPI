package com.example.FileShareAPI.Back_End.service;

import com.example.FileShareAPI.Back_End.dto.FileDto;
import com.example.FileShareAPI.Back_End.dto.FileUploadDto;
import com.example.FileShareAPI.Back_End.exception.InsufficientStorageException;
import com.example.FileShareAPI.Back_End.exception.ResourceNotFoundException;
import com.example.FileShareAPI.Back_End.exception.UnAuthorizedException;
import com.example.FileShareAPI.Back_End.model.File;
import com.example.FileShareAPI.Back_End.model.User;
import com.example.FileShareAPI.Back_End.repo.FileRepo;
import com.example.FileShareAPI.Back_End.repo.UserRepo;
import com.example.FileShareAPI.Back_End.specification.FileSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import utils.UserUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.FileShareAPI.Back_End.constant.Constant.FILE_DIRECTORY;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static utils.FileUtils.*;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepo fileRepo;
    private final UserRepo userRepo;

    @Transactional
    public FileDto createFile(FileUploadDto fileUploadDto) throws IOException {
        MultipartFile file = fileUploadDto.getFile();
        String originalFilename = fileUploadDto.getOriginalFilename();
        String extension = fileUploadDto.getExtension();
        String customFilename = fileUploadDto.getCustomFilename();
        String description = fileUploadDto.getDescription();
        boolean isPublic = fileUploadDto.isPublic();

        String userId = UserUtils.getUserIdfromContext();
        User fileOwner = userRepo.getReferenceById(userId);

        long fileSize = file.getSize();

        File fileObject = new File(fileOwner,
                extension,
                originalFilename,
                fileSize,
                bytesToHumanReadable(fileSize),
                customFilename,
                description,
                isPublic);// Creating the file object

        fileRepo.save(fileObject); // saving the file to the database
        String newFileName = fileObject.getFileId() + "." + extension;
        saveFile(newFileName, file, fileOwner);


        return fileObject.toDto(false, true);
    }

    public InputStreamResource getFileContent(String fileId) throws IOException {
        File file = getFileById(fileId);
        //If the file is not public or the logged-in user is not the owner of the file, then he may not access it.
        hasAccessToFile(file); // If the user does not have access to the file, an exception gets thrown

        InputStreamResource resource = new InputStreamResource(new FileInputStream(String.valueOf(file.getFilePath())));

        return resource;
    }

    public void hasAccessToFile(File file) {
        if (!(file.getIsPublic() || Objects.equals(file.getUser().getUserId(), UserUtils.getUserIdfromContext()))) {
            throw new UnAuthorizedException("You don't have access to this file");
        }
    }

    public HttpHeaders createHeader(String fileId) throws IOException {
        File file = getFileById(fileId);
        String fileNameWithExtension = file.getFileNameWithExtension();
        String contentType = Files.probeContentType(file.getFilePath());
        if (contentType == null) contentType = "application/octet-stream";// Default to binary stream if type is unknown

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.parseMediaType(contentType));
        header.setContentDispositionFormData("attachment", fileNameWithExtension);

        return header;
    }

    //Verifies if the file exists and if the user has access to the file
    public File getFileById(String fileId) {
        File file = fileRepo.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("The specified file was not found"));
        hasAccessToFile(file);
        return file;
    }

    public Page<FileDto> getFilesByKeyword(String keyword,
                                           String sorting,
                                           String owner,
                                           String extension,
                                           int page,
                                           int size) {
        String userId = UserUtils.getUserIdfromContext();
        page = Math.max(0, page); //Min page nr 0
        size = Math.min(size, 30); //Max page size 30
        Sort sortingMethod = findSorting(sorting);

        Pageable pageable = PageRequest.of(page, size, sortingMethod);

        Specification<File> spec = Specification.where(FileSpecifications.hasKeyword(keyword))
                        .and(FileSpecifications.isAccessible(userId))
                        .and(FileSpecifications.hasExtension(extension))
                        .and(FileSpecifications.filterByOwner(owner, userId));

        Page<File> filesPage = fileRepo.findAll(spec, pageable);
        return filesPage.map(file -> file.toDto(true, isFileOwner(file)));
    }


    public FileDto getFileDescription(String fileId) {
        File file = getFileById(fileId);
        hasAccessToFile(file); // will throw an exception, if the user does not have access.
        FileDto fileDto = file.toDto(false, isFileOwner(file));

        return fileDto;
    }

    @Transactional
    public FileDto updateFile(FileUploadDto fileUploadDto) throws IOException {
        String fileId = fileUploadDto.getFileId();
        MultipartFile newFile = fileUploadDto.getFile();
        String customFilename = fileUploadDto.getCustomFilename();
        String description = fileUploadDto.getDescription();
        boolean isPublic = fileUploadDto.isPublic();
        String userId = UserUtils.getUserIdfromContext();
        File fileToUpdate = verifyFileOwnership(userId, fileId); // If the signed-in user does not own this file, then an exception gets thrown
        User fileOwner = userRepo.getReferenceById(userId);

        if (newFile != null) {
            deleteFile(userId, fileId); //If a new file was uploaded, delete the previous file.

            String originalFilename = fileUploadDto.getOriginalFilename();
            String extension = fileUploadDto.getExtension();
            fileToUpdate.setFileExtension(extension);

            saveFile(fileToUpdate.getFileNameWithExtension(), newFile, fileOwner);

            if (stringIsNullorBlank(customFilename)) { //If no custom name was specified, use the files original name
                originalFilename = originalFilename.substring(0, originalFilename.lastIndexOf(".")); //TODO: if the file has no name/extension, an error occurs // Move to File class
                fileToUpdate.setFileName(originalFilename);
            }

            long fileSize = newFile.getSize();
            fileToUpdate.setSizeBytes(fileSize);
            fileToUpdate.setSizeHumanReadable(bytesToHumanReadable(fileSize));
        }

        if (!stringIsNullorBlank(customFilename)) fileToUpdate.setFileName(customFilename);
        if (!stringIsNullorBlank(description)) fileToUpdate.setDescription(description);
        fileToUpdate.setIsPublic(isPublic);


        fileToUpdate.setTimestamp(LocalDateTime.now(ZoneId.of("Europe/Tallinn")));
        //TODO: create another db table for logging operations made with the file
        File updatedFile = fileRepo.save(fileToUpdate);

        return updatedFile.toDto(false, true);
    }

    @Transactional
    public void handleDeleteFileRequest(String fileId) throws IOException {
        String userId = UserUtils.getUserIdfromContext();
        deleteFile(userId, fileId);
        fileRepo.deleteById(fileId);
    }

    @Transactional
    public void deleteFile(String userId, String fileId) throws IOException {
        File fileToDelete = verifyFileOwnership(userId, fileId);
        User fileOwner = userRepo.getReferenceById(userId);
        //TODO: create a class method for finding filestoragelocation.

        String customFile_Directory = FILE_DIRECTORY + userId + "/" + fileToDelete.getFileNameWithExtension(); // Custom folder for each user
        Path fileStorageLocation = Paths.get(customFile_Directory).toAbsolutePath().normalize();

        Files.delete(fileStorageLocation);
        // Update total used user memory.
        Long fileSizeBytes = fileToDelete.getSizeBytes();
        if (fileSizeBytes != null) {
            updateUserUsedMemory(fileOwner, -(fileSizeBytes));
        }
    }


    private File verifyFileOwnership(String userId, String fileId) {
        User fileOwner = userRepo.getReferenceById(userId);
        // If fileToUpdate is null, then the user is not allowed to manipulate the file/ the file doesn't exist.
        return fileRepo.findByUserAndFileId(fileOwner, fileId)
                .orElseThrow(() -> new UnAuthorizedException("You are not authorized to modify this file"));
    }


    //TODO: query only file_extensions 
    public Set<String> getFileExtensions() {
        String userId = UserUtils.getUserIdfromContext();
        Specification<File> spec = Specification.where(FileSpecifications.isAccessible(userId));
        return fileRepo.findAll(spec).stream()
                .map(File::getFileExtension)
                .collect(Collectors.toSet());
    }

    @Transactional
    public void saveFile(String fileName, MultipartFile file, User user) throws IOException {
        hasEnoughFreeSpace(user, file.getSize()); // checks if the user has enough free space, throws an exception if not.

        String customFileDirectory = FILE_DIRECTORY + user.getUserId() + "/"; // Custom folder for each user
        Path fileStorageLocation = Paths.get(customFileDirectory).toAbsolutePath().normalize();

        if (!Files.exists(fileStorageLocation)) {
            Files.createDirectories(fileStorageLocation);
        } // If there is no such directory, create it

        Files.copy(file.getInputStream(), fileStorageLocation.resolve(fileName), REPLACE_EXISTING);
        updateUserUsedMemory(user, file.getSize());
    }

    public void hasEnoughFreeSpace(User user, long fileSize) {
        long allowedSpace = user.getRole().getTotalAvailableBytes();
        long usedSpace = user.getTotalMemoryUsedBytes();
        if (usedSpace + fileSize > allowedSpace) {
            throw new InsufficientStorageException("Insufficient free space. You have " +
                    bytesToHumanReadable(allowedSpace - usedSpace) + " of free space left");
        }
    }

    // For the delete operation, fileSizeBytes is negative.
    @Transactional
    public void updateUserUsedMemory(User user, Long fileSizeBytes) {
        Long usedMemoryBytes = user.getTotalMemoryUsedBytes();
        if (usedMemoryBytes == null) usedMemoryBytes = 0L;

        long updatedMemoryBytes = usedMemoryBytes + fileSizeBytes;
        String updatedMemoryHumanReadable = bytesToHumanReadable(updatedMemoryBytes);

        user.setTotalMemoryUsedBytes(updatedMemoryBytes);
        user.setTotalMemoryUsedHumanReadable(updatedMemoryHumanReadable);

        userRepo.save(user);
    }

    public boolean isFileOwner(File file) {
        String userId = UserUtils.getUserIdfromContext();
        return file.getUser().getUserId().equals(userId);
    }

}
