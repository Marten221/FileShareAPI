package com.example.FileShareAPI.Back_End.dto;

import lombok.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import static utils.FileUtils.stringIsNullorBlank;
import static utils.FileUtils.truncateString;

@AllArgsConstructor
@Getter
@Setter
@RequiredArgsConstructor
public class FileUploadDto {
    String fileId;
    MultipartFile file;
    String customFilename;
    String description;
    boolean isPublic;

    public String getCustomFilename() {
        if (stringIsNullorBlank(customFilename)){
            customFilename = this.getOriginalFilename();
        }

        //You need to truncate the custom name, if customFilename != originalFilename
        customFilename = truncateString(customFilename, 255);

        return customFilename;
    }

    public String getDescription() {
        if (!stringIsNullorBlank(description)) description = truncateString(description, 1000);
        return description;
    }

    public String getOriginalFilename(){
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) return null;
        return originalFilename.substring(0, originalFilename.indexOf("."));
    }

    public String getExtension(){
        if (file == null) return null;
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) return null;
        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
    }

}
