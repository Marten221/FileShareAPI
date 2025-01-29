package com.example.FileShareAPI.Back_End.model;

import com.example.FileShareAPI.Back_End.dto.DiskSpaceDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import utils.UserUtils;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @UuidGenerator
    @Column(unique = true, updatable = false)
    private String userId;
    private String firstName;
    private String lastName;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<File> files;
    private Long totalMemoryUsedBytes = 0L;
    private String totalMemoryUsedHumanReadable;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    @PrePersist
    @PreUpdate
    public void sanitizeEmail() {
        if (email != null) email = email.toLowerCase();
    };

    public DiskSpaceDto getUserDiskSpace() {
        return new DiskSpaceDto(this.totalMemoryUsedBytes, this.role.getTotalAvailableBytes(), this.totalMemoryUsedHumanReadable, this.role.getTotalAvailableBytesHumanReadable());
    }
}
