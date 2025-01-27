package com.example.FileShareAPI.Back_End.repo;

import com.example.FileShareAPI.Back_End.model.RegistrationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationCodeRepo extends JpaRepository<RegistrationCode, Long> {
    RegistrationCode findByCode(String code);
}
