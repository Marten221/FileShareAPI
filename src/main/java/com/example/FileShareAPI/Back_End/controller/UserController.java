package com.example.FileShareAPI.Back_End.controller;

import com.example.FileShareAPI.Back_End.dto.DiskSpaceDto;
import com.example.FileShareAPI.Back_End.model.User;
import com.example.FileShareAPI.Back_End.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @PostMapping("/public/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        String token = userService.registerUser(user);
        return ResponseEntity.ok().body("{\"token\":\"" + token + "\"}");
    }

    @PostMapping("/public/login")
    public ResponseEntity<String> loginUser(@RequestBody User userCredentials) {
        String token = userService.loginUser(userCredentials);
        return ResponseEntity.ok().body("{\"token\":\"" + token + "\"}");
    }


    @GetMapping({"/diskspace", "/diskspace/{id}"}) //TODO: return in human readable format
    public ResponseEntity<DiskSpaceDto> getDiskSpace(@PathVariable(value = "id", required = false) String id) throws IOException {
        return ResponseEntity.ok().body(userService.findDiskUsage(id));
    }

}
