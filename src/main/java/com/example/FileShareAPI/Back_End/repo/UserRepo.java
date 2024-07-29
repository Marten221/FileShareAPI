package com.example.FileShareAPI.Back_End.repo;

import com.example.FileShareAPI.Back_End.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, String> {
}
