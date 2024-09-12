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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.FileShareAPI.Back_End.constant.Constant.FILE_DIRECTORY;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepo fileRepo;
    private final UserRepo userRepo;

    private static final Map<String, Sort> SORTING_MAP = new HashMap<>();
    static {
        SORTING_MAP.put("name_ascending", Sort.by("fileName").ascending()); // Specification doesn't support snake case?? only read file from file_name
        SORTING_MAP.put("name_descending", Sort.by("fileName").descending()); // Specification needs the class field name not the field name from the database table
        SORTING_MAP.put("date_ascending", Sort.by("timestamp").ascending());
        SORTING_MAP.put("date_descending", Sort.by("timestamp").descending());
        SORTING_MAP.put("size_ascending", Sort.by("sizeBytes").ascending());
        SORTING_MAP.put("size_descending", Sort.by("sizeBytes").descending());
    }

    public FileDto createFile(String id,
                              MultipartFile file,
                              String customFilename,
                              String description,
                              Boolean isPublic) throws IOException {
        if (customFilename.length() > 255) customFilename = customFilename.substring(0, 255); //TODO: check if it is null before
        if (description.length() > 1000) description = description.substring(0, 1000);

        User fileOwner = userRepo.getReferenceById(id);
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null; //TODO: remove for production
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        long fileSize = file.getSize();

        File fileObject = createFileObject(fileOwner,
                extension,
                originalFilename,
                fileSize,
                bytesToHumanReadable(fileSize),
                customFilename,
                description,
                isPublic);// Creating the file and setting its values

        fileRepo.save(fileObject); // saving the file to the database and file system
        String newFileName = fileObject.getFileId() + "." + extension;
        saveFile(newFileName, file, id);

        return fileToDto(fileObject, false);
    }

    private static File createFileObject(User fileOwner,
                                         String extension,
                                         String originalFilename,
                                         long size,
                                         String sizeHumanReadable,
                                         String customFileName,
                                         String desc,
                                         boolean isPublic) {
        File fileObject = new File();
        originalFilename = originalFilename.substring(0, originalFilename.lastIndexOf(".")); //TODO: if the file has no name/extension, an error occurs
        if (originalFilename.length() > 255) originalFilename = originalFilename.substring(0, 255);

        fileObject.setUser(fileOwner);
        fileObject.setFileExtension(extension);
        fileObject.setFileName(originalFilename); //TODO: remove file ext from the original filename
        fileObject.setSizeBytes(size);
        fileObject.setSizeHumanReadable(sizeHumanReadable);
        fileObject.setFileName(customFileName == null || customFileName.isBlank() ? originalFilename : customFileName); // If no custom name is given, use the original file Name
        fileObject.setDescription(desc);
        fileObject.setIsPublic(isPublic);
        fileObject.setTimestamp(LocalDateTime.now());

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

    private FileDto fileToDto(File fileObject, boolean previewBool) {
        String descriptionString = fileObject.getDescription();
        String description;
        if (previewBool && descriptionString.length() > 100) description = descriptionString.substring(0, 100) + "..."; // If the front end request data for cards, then the full desc is not needed
        else description = descriptionString;

        return new FileDto(
                fileObject.getFileId(),
                fileObject.getFileName(),
                fileObject.getFileExtension(),
                fileObject.getSizeHumanReadable(),
                description,
                fileObject.getTimestamp()
        );
    }

    public static String bytesToHumanReadable(long bytes) {
        if (bytes < 1000) {
            return bytes + " B";
        }
        int unit = 1000;
        String[] units = {"KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};

        // Calculate the index of the unit
        int index = (int) (Math.log(bytes) / Math.log(unit));
        // Round the bytes to two decimal places
        double value = bytes / Math.pow(unit, index);

        return String.format("%.2f %s", value, units[index - 1]);
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

    private Path getFilePath(String fileName) {
        File file = fileRepo.getReferenceById(fileName);
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
        return filesPage.map(file -> fileToDto(file, true));
    }

    private Sort findSorting(String sorting) {
        return SORTING_MAP.getOrDefault(sorting, Sort.by("file_name").ascending());
    }

    public FileDto getFileDescription(String fileId) {
        return fileToDto(fileRepo.getReferenceById(fileId), false);
    }


    public Set<String> getFileExtensions(String userId) {
        Specification<File> spec = Specification.where(FileSpecifications.isAccessible(userId));
        return fileRepo.findAll(spec).stream()
                .map(File::getFileExtension)
                .collect(Collectors.toSet());
    }
}
