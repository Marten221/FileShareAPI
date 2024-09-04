package com.example.FileShareAPI.Back_End.repo;

import com.example.FileShareAPI.Back_End.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface FileRepo extends JpaRepository<File, String>, JpaSpecificationExecutor<File> {

    @Query(value = "SELECT distinct file_extension FROM files f", nativeQuery = true) //TODO: Where userid == Loggedinuser
    Set<String> getFileExtensions();

}
