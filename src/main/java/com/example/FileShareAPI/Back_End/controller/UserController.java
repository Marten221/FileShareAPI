package com.example.FileShareAPI.Back_End.controller;

import com.example.FileShareAPI.Back_End.model.User;
import com.example.FileShareAPI.Back_End.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @PostMapping("/public/register")
    public ResponseEntity<String> registerUser(@RequestBody User user,
                                               @RequestHeader("X-Registration-Code") String registrationCode) {
        String token = userService.registerUser(user, registrationCode);
        return ResponseEntity.ok().body("{\"token\":\"" + token + "\"}");
    }

    @PostMapping("/public/login")
    public ResponseEntity<String> loginUser(@RequestBody User userCredentials) {
        String token = userService.loginUser(userCredentials);
        return ResponseEntity.ok().body("{\"token\":\"" + token + "\"}");
    }


    /*@GetMapping("/diskspace") //TODO: return in human readable format
    public ResponseEntity<DiskSpaceDto> getDiskSpace() throws IOException {
        return ResponseEntity.ok().body(userService.findDiskUsage());
    }*/

    @GetMapping("/admin/updatememory")
    public ResponseEntity<String> updateUserTotalMemory() {
        return ResponseEntity.ok().body(userService.recalculateTotalMemory());
    }

}
