package com.example.FileShareAPI.Back_End.repo;

import com.example.FileShareAPI.Back_End.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role, Long> {
    Role getRoleByRole(String role);
}
