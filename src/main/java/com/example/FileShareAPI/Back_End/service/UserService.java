package com.example.FileShareAPI.Back_End.service;

import com.example.FileShareAPI.Back_End.dto.DiskSpaceDto;
import com.example.FileShareAPI.Back_End.dto.UserDto;
import com.example.FileShareAPI.Back_End.exception.InvalidCredentialsException;
import com.example.FileShareAPI.Back_End.model.File;
import com.example.FileShareAPI.Back_End.model.Role;
import com.example.FileShareAPI.Back_End.model.User;
import com.example.FileShareAPI.Back_End.repo.FileRepo;
import com.example.FileShareAPI.Back_End.repo.RoleRepo;
import com.example.FileShareAPI.Back_End.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utils.FileUtils;
import utils.JwtUtil;
import utils.UserUtils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final RegistrationCodeService registrationCodeService;
    private final FileRepo fileRepo;
    private final RoleRepo roleRepo;

    @Transactional
    public String registerUser(User user, String code) {
        validateRegistration(user, code);

        String rawPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(rawPassword));
        Role role = roleRepo.getRoleByRole("USER");
        user.setRole(role); // set the role to USER
        user = userRepo.save(user);
        registrationCodeService.setRegistrationCodeAsUsed(code);

        return JwtUtil.generateToken(user.getUserId(), role);
    }

    public void validateRegistration(User user, String code) {
        if (!UserUtils.validateEmail(user.getEmail())) {
            throw new InvalidCredentialsException("Incorrect email");
        }
        if (userRepo.findByEmail(user.getEmail()) != null){
            throw new InvalidCredentialsException("This email is already in use");
        }
        registrationCodeService.validateCode(code);
    }

    public String loginUser(User userCredentials) {
        String email = userCredentials.getEmail().toLowerCase();
        String rawPassword = userCredentials.getPassword();
        User user = userRepo.findByEmail(email);
        if (user == null || !passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        return JwtUtil.generateToken(user.getUserId(), user.getRole());
    }

    public DiskSpaceDto findDiskUsage() {
        String userId = UserUtils.getUserIdfromContext();
        User user = userRepo.getReferenceById(userId);
        return user.getUserDiskSpace();
    }

    //TODO check roles
    public Boolean getLoginStatus() {
        String userId = UserUtils.getUserIdfromContext();
        return !userId.equalsIgnoreCase("anonymoususer");
    }

    public UserDto getUserInfo() {
        String userId = UserUtils.getUserIdfromContext();
        User user = userRepo.getReferenceById(userId);
        return new UserDto(user.getFirstName());
    }

    public void recalculateTotalMemory() {
        List<User> users = userRepo.findAll();
        for (User user : users) {
            List<File> usersFiles = fileRepo.findAllByUser(user);
            Long totalMemory = 0L;
            for (int i = 0; i < usersFiles.size(); i++) {
                File file = usersFiles.get(i);
                totalMemory += file.getSizeBytes();
            }
            user.setTotalMemoryUsedBytes(totalMemory);
            user.setTotalMemoryUsedHumanReadable(FileUtils.bytesToHumanReadable(totalMemory));
            userRepo.save(user);
        }
    }
}
