package com.example.FileShareAPI.Back_End.model;

import com.example.FileShareAPI.Back_End.dto.FileDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

import static utils.FileUtils.stringIsNullorBlank;
import static utils.FileUtils.truncateString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "files")
public class File {
    @Id
    @UuidGenerator
    @Column(unique = true, updatable = false) //Unique automatically creates an index
    private String fileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;
    private String fileExtension;
    private String fileName;
    @Nullable
    private Long sizeBytes;
    private String sizeHumanReadable;
    @Column(length = 1000)
    private String description;
    private LocalDateTime timestamp;
    private Boolean isPublic = false;
//    @Lob
//    @Column(length = 20971520)//20MB
//    private byte[] photo;

    //TODO: custom file name, file description, timestamp, etc.


    public File(User fileOwner,
                String extension,
                String originalFilename,
                long size,
                String sizeHumanReadable,
                String customFileName,
                String desc,
                boolean isPublic) {
        this.user = fileOwner;
        this.fileExtension = extension;
        this.fileName = stringIsNullorBlank(customFileName) ? originalFilename : customFileName;
        this.sizeBytes = size;
        this.sizeHumanReadable = sizeHumanReadable;
        this.description = desc;
        this.timestamp = LocalDateTime.now();
        this.isPublic = isPublic;
    }

    public FileDto toDto(boolean previewBool) {
        String descriptionData;

        if (previewBool) {
            descriptionData = truncateString(description, 100); // If the front end requests data for cards, then the full desc is not needed
            if (description.length() > 100) descriptionData += "...";
        } else descriptionData = description;

        return new FileDto(
                fileId,
                fileName,
                fileExtension,
                sizeHumanReadable,
                descriptionData,
                timestamp,
                isPublic
        );
    }
}
