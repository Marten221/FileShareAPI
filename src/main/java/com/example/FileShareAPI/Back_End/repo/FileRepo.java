package com.example.FileShareAPI.Back_End.repo;

import com.example.FileShareAPI.Back_End.model.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepo extends JpaRepository<File, String> {
    //https://github.com/hendisantika/Spring-Boot-Search-Example-using-Thymeleaf-and-Spring-Data-JPA/tree/main
    @Query(value = "SELECT * FROM files f WHERE LOWER(f.file_name) LIKE LOWER(CONCAT('%', :keyword, '%'))", nativeQuery = true)//TODO: implement checking the id as well.
    Page<File> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
