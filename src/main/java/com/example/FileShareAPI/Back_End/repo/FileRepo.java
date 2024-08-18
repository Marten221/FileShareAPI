package com.example.FileShareAPI.Back_End.repo;

import com.example.FileShareAPI.Back_End.model.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface FileRepo extends JpaRepository<File, String> {
    //https://github.com/hendisantika/Spring-Boot-Search-Example-using-Thymeleaf-and-Spring-Data-JPA/tree/main
    @Query(value = "SELECT * FROM files f WHERE (%:keyword% = '*' OR LOWER(f.file_name) LIKE LOWER(CONCAT('%', :keyword, '%')))" +
            "AND (%:extension% = 'any' OR f.file_extension = %:extension%)", nativeQuery = true)//TODO: implement checking the id as well.
    Page<File> findByKeyword(@Param("keyword") String keyword, @Param("extension") String extension, Pageable pageable);

    @Query(value = "SELECT distinct file_extension FROM files f", nativeQuery = true) //TODO: Where userid == Loggedinuser
    Set<String> getFileExtensions();
}
