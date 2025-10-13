package com.example.FileShareAPI.Back_End.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public boolean isExpired() {
        return this.expiresAt.isBefore(LocalDateTime.now(ZoneId.of("Europe/Tallinn")));
    }
}
