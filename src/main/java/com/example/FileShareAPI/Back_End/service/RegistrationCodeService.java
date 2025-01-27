package com.example.FileShareAPI.Back_End.service;

import com.example.FileShareAPI.Back_End.exception.RegistrationCodeInvalidException;
import com.example.FileShareAPI.Back_End.model.RegistrationCode;
import com.example.FileShareAPI.Back_End.repo.RegistrationCodeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegistrationCodeService {
    private final RegistrationCodeRepo registrationCodeRepo;

    public void validateCode(String code) {
        RegistrationCode registrationCode = registrationCodeRepo.findByCode(code);
        if (registrationCode == null) throw new RegistrationCodeInvalidException("Registration code not found", HttpStatus.NOT_FOUND);
        if (registrationCode.isUsed()) throw new RegistrationCodeInvalidException("Registratin code has already been used", HttpStatus.CONFLICT);
        if (registrationCode.isExpired()) throw new RegistrationCodeInvalidException("Registration code has expired", HttpStatus.GONE);
        //If no errors occur, then the code is valid.
    }

    public void setRegistrationCodeAsUsed(String code) {
        RegistrationCode registrationCode = registrationCodeRepo.findByCode(code);
        registrationCode.setUsed(true);
        registrationCodeRepo.save(registrationCode);
    }
}
