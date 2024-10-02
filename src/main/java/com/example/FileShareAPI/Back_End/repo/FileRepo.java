package com.example.FileShareAPI.Back_End.repo;

import com.example.FileShareAPI.Back_End.model.File;
import com.example.FileShareAPI.Back_End.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepo extends JpaRepository<File, String>, JpaSpecificationExecutor<File> {
    File findByUserAndFileId(User user, String fileId);
}
