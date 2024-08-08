package com.example.FileShareAPI.Back_End.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

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
    @Column(length = 1000)
    private String description;
    private LocalDateTime timestamp;
//    @Lob
//    @Column(length = 20971520)//20MB
//    private byte[] photo;

    //TODO: custom file name, file description, timestamp, etc.
}
