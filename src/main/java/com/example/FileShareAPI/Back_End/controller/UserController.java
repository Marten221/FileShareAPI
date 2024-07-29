package com.example.FileShareAPI.Back_End.controller;

import com.example.FileShareAPI.Back_End.dto.DiskSpaceDto;
import com.example.FileShareAPI.Back_End.model.User;
import com.example.FileShareAPI.Back_End.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.created(URI.create("/users/userId")).body(userService.saveUser(user));
    }



    @GetMapping({"/diskspace", "/diskspace/{id}"})
    public ResponseEntity<DiskSpaceDto> getDiskSpace(@PathVariable(value = "id", required = false) String id) throws IOException {
        return ResponseEntity.ok().body(userService.findDiskUsage(id));
    }



}
