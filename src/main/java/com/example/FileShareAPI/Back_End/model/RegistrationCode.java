package com.example.FileShareAPI.Back_End.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "registration_codes")
public class RegistrationCode {
    @Id
    @Column(unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String code;
    private boolean isUsed;
    private LocalDateTime expiresAt;

    public RegistrationCode(String code, boolean isUsed, LocalDateTime expiresAt) {
        this.code = code;
        this.isUsed = isUsed;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return this.expiresAt.isBefore(LocalDateTime.now(ZoneId.of("Europe/Tallinn")));
    }
}
