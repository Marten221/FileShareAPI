package com.example.FileShareAPI.Back_End.service;

import com.example.FileShareAPI.Back_End.dto.DiskSpaceDto;
import com.example.FileShareAPI.Back_End.exception.InvalidCredentialsException;
import com.example.FileShareAPI.Back_End.model.User;
import com.example.FileShareAPI.Back_End.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import utils.JwtUtil;
import utils.UserUtils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicLong;

import static com.example.FileShareAPI.Back_End.constant.Constant.FILE_DIRECTORY;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public String registerUser(User user) {
        if (!UserUtils.validateEmail(user.getEmail())) {
            throw new InvalidCredentialsException("Incorrect email");
        }
        if (userRepo.findByEmail(user.getEmail()) != null){
            throw new InvalidCredentialsException("This email is already in use");
        }
        String rawPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(rawPassword));
        user = userRepo.save(user);

        return JwtUtil.generateToken(user.getUserId());
    }

    public String loginUser(User userCredentials) {
        String email = userCredentials.getEmail();
        String rawPassword = userCredentials.getPassword();
        User user = userRepo.findByEmail(email);
        if (user == null || !passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        return JwtUtil.generateToken(user.getUserId());
    }

    public DiskSpaceDto findDiskUsage(String id) throws IOException {
        String directory = FILE_DIRECTORY;
        if (id != null) directory += id;
        Path path = Paths.get(directory);
        FileStore store = Files.getFileStore(path);
            // If the user has not uploaded any files, then the user does not have their own folder yet.
        return new DiskSpaceDto(store.getUsableSpace(), store.getTotalSpace(), size(path));
    }



    /**
     * Attempts to calculate the size of a file or directory.
     * https://stackoverflow.com/questions/2149785/get-size-of-folder-or-file
     * <p>
     * Since the operation is non-atomic, the returned value may be inaccurate.
     * However, this method is quick and does its best.
     */
    public static long size(Path path) {

        final AtomicLong size = new AtomicLong(0);

        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

                    size.addAndGet(attrs.size());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {

                    System.out.println("skipped: " + file + " (" + exc + ")");
                    // Skip folders that can't be traversed
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {

                    if (exc != null)
                        System.out.println("had trouble traversing: " + dir + " (" + exc + ")");
                    // Ignore errors traversing a folder
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new AssertionError("walkFileTree will not throw IOException if the FileVisitor does not");
        }

        return size.get();
    }
}
