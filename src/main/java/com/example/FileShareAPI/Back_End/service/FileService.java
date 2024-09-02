package com.example.FileShareAPI.Back_End.service;

import com.example.FileShareAPI.Back_End.dto.FileDescriptionDto;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.example.FileShareAPI.Back_End.constant.Constant.FILE_DIRECTORY;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepo fileRepo;
    private final UserRepo userRepo;

    private static final Map<String, Sort> SORTING_MAP = new HashMap<>();
    static {
        SORTING_MAP.put("name_ascending", Sort.by("file_name").ascending());
        SORTING_MAP.put("name_descending", Sort.by("file_name").descending());
        SORTING_MAP.put("date_ascending", Sort.by("timestamp").ascending());
        SORTING_MAP.put("date_descending", Sort.by("timestamp").descending());
        SORTING_MAP.put("size_ascending", Sort.by("size_bytes").ascending());
        SORTING_MAP.put("size_descending", Sort.by("size_bytes").descending());
    }

    public FilePreviewDto createFile(String id, MultipartFile file, String customFilename, String description) throws IOException {
        if (customFilename.length() > 255) customFilename = customFilename.substring(0, 255);
        if (description.length() > 1000) description = description.substring(0, 1000);

        User fileOwner = userRepo.getReferenceById(id);
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null; //TODO: remove for production
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

        File fileObject = createFileObject(fileOwner, extension, originalFilename, file.getSize(), customFilename, description, LocalDateTime.now());// Creating the file and setting its values

        fileRepo.save(fileObject); // saving the file to the database and file system
        String newFileName = fileObject.getFileId() + "." + extension;
        saveFile(newFileName, file, id);

        return fileToPreviewDto(fileObject);
    }

    private static File createFileObject(User fileOwner, String extension, String originalFilename, long size, String customFileName, String desc, LocalDateTime timestamp) {
        File fileObject = new File();
        originalFilename = originalFilename.substring(0, originalFilename.lastIndexOf(".")); //TODO: if the file has no name/extension, an error occurs
        if (originalFilename.length() > 255) originalFilename = originalFilename.substring(0, 255);


        fileObject.setUser(fileOwner);
        fileObject.setFileExtension(extension);
        fileObject.setFileName(originalFilename); //TODO: remove file ext from the original filename
        fileObject.setSizeBytes(size);
        fileObject.setFileName(customFileName == null || customFileName.isBlank() ? originalFilename : customFileName); // If no custom name is given, use the original file Name
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
        return new FilePreviewDto(
                fileObject.getFileId(),
                fileObject.getUser().getUserId(),
                fileObject.getFileName(),
                fileObject.getFileExtension(),
                fileObject.getTimestamp()
        );
    }

    private FileDescriptionDto fileToDescriptionDto(File fileObject) {
        return new FileDescriptionDto(
                fileObject.getFileId(),
                fileObject.getUser().getUserId(),
                fileObject.getFileName(),
                bytesToHumanReadable(Optional.ofNullable(fileObject.getSizeBytes()).orElse(0L)),
                fileObject.getTimestamp(),
                fileObject.getFileExtension(),
                fileObject.getDescription()
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

    public byte[] getFileContent(String fileId) throws IOException {
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

    public Page<FilePreviewDto> getFilesByKeyword(String keyword, String sorting, String extension, int page, int size) {
        System.out.println("keyword" + keyword);

        page = Math.max(0, page); //Min page nr 0
        size = Math.min(size, 30); //Max page size 30
        Sort sortingMethod = findSorting(sorting);
        Pageable pageable = PageRequest.of(page, size, sortingMethod);
        System.out.println(keyword + "  " + extension);
        Page<File> filesPage = fileRepo.findByKeyword(keyword, extension, pageable);
        return filesPage.map(file -> fileToPreviewDto(file));
    }

    private Sort findSorting(String sorting) {
        return SORTING_MAP.getOrDefault(sorting, Sort.by("file_name").ascending());
    }

    public FileDescriptionDto getFileDescription(String fileId) {
        return fileToDescriptionDto(fileRepo.getReferenceById(fileId)); //TODO: FileDescriptionDto
    }


    public Set<String> getFileExtensions() {
        return fileRepo.getFileExtensions();
    }
}
