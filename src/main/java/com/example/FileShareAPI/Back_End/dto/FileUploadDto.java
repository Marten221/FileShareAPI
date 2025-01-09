package com.example.FileShareAPI.Back_End.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import static utils.FileUtils.stringIsNullorBlank;
import static utils.FileUtils.truncateString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FileUploadDto {
    String fileId;
    MultipartFile file;
    String customFilename;
    String description;
    boolean isPublic = false;

    public String getCustomFilename() {
        String originalFilename = this.getOriginalFilename();
        if (stringIsNullorBlank(customFilename)){
            customFilename = truncateString(originalFilename, originalFilename.lastIndexOf("."));
        }

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
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) return null;
        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        //return truncateString(originalFilename, originalFilename.lastIndexOf(".") + 1);
    }

}
