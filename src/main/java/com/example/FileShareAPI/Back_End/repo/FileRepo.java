package com.example.FileShareAPI.Back_End.repo;

import com.example.FileShareAPI.Back_End.model.File;
import com.example.FileShareAPI.Back_End.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface FileRepo extends JpaRepository<File, String>, JpaSpecificationExecutor<File> {
    Optional<File> findByUserAndFileId(User user, String fileId);

    @Query("SELECT DISTINCT f.fileExtension FROM File f WHERE f.isPublic or f.user.userId = :userId")
    Set<String> findAllDistinctExtensions(@Param("userId") String userId);
}