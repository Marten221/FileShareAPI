package com.example.FileShareAPI.Back_End.dto;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String email;
    private String password;
}
