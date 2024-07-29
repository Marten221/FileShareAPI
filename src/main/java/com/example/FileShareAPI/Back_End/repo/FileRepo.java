package com.example.FileShareAPI.Back_End.repo;

import com.example.FileShareAPI.Back_End.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepo extends JpaRepository<File, String> {
    //https://github.com/hendisantika/Spring-Boot-Search-Example-using-Thymeleaf-and-Spring-Data-JPA/tree/main
    @Query(value = "SELECT * FROM files f WHERE f.file_id LIKE %:keyword%", nativeQuery = true)//Actually this query is written correctly
    List<File> findByKeyword(@Param("keyword") String keyword);
}
