package com.example.FileShareAPI.Back_End.controller;

import com.example.FileShareAPI.Back_End.dto.DiskSpaceDto;
import com.example.FileShareAPI.Back_End.dto.LoginStatusDto;
import com.example.FileShareAPI.Back_End.dto.UserDto;
import com.example.FileShareAPI.Back_End.model.User;
import com.example.FileShareAPI.Back_End.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utils.JwtUtil;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @PostMapping("/public/register")
    public ResponseEntity<Void> registerUser(@RequestBody User user,
                                               @RequestHeader("X-Registration-Code") String registrationCode) {
        return userService.registerUser(user, registrationCode);
    }

    @PostMapping("/public/login")
    public ResponseEntity<Void> loginUser(@RequestBody User userCredentials) {
        return userService.loginUser(userCredentials);
    }

    @GetMapping("/public/loginstatus")
    public ResponseEntity<LoginStatusDto> checkLoginStatus() {
        boolean loggedIn = userService.getLoginStatus();
        return ResponseEntity.ok().body(new LoginStatusDto(loggedIn));
    }

    @GetMapping("/userinfo")
    public ResponseEntity<UserDto> getUserInfo() {
        UserDto userDto = userService.getUserInfo();
        return ResponseEntity.ok().body(userDto);
    }

    @GetMapping("/diskspace")
    public ResponseEntity<DiskSpaceDto> getDiskSpace() {
        return ResponseEntity.ok().body(userService.findDiskUsage());
    }

    @PostMapping("/public/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        ResponseCookie cookie = JwtUtil.generateLogoutCookie();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().build();
    }
}