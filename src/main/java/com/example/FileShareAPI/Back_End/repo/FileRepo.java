package com.example.FileShareAPI.Back_End.repo;

import com.example.FileShareAPI.Back_End.model.File;
import com.example.FileShareAPI.Back_End.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepo extends JpaRepository<File, String>, JpaSpecificationExecutor<File> {
    Optional<File> findByUserAndFileId(User user, String fileId);
    List<File> findAllByUser(User user);
}
