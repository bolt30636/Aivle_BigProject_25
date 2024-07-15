package com.example.BigProject_25.controller;

import com.example.BigProject_25.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public boolean login(@RequestParam String userID, @RequestParam String password, @RequestParam String captchaResponse) {
        return authService.login(userID, password, captchaResponse);
    }

    @PostMapping("/signup")
    public boolean signup(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String userID,
            @RequestParam int phoneNum,
            @RequestParam String password,
            @RequestParam String userType,
            @RequestParam String captchaResponse) {
        return authService.signup(name, email, userID, phoneNum, password, userType, captchaResponse);
    }

    @PostMapping("/social-login")
    public boolean socialLogin(@RequestParam String provider, @RequestParam String token) {
        return authService.socialLogin(provider, token);
    }

    @GetMapping("/token/name")
    public String getNameFromToken() {
        return authService.getNameFromToken();
    }

    @GetMapping("/token/email")
    public String getEmailFromToken() {
        return authService.getEmailFromToken();
    }

    @GetMapping("/logout")
    public boolean logout() {
        return authService.logout();
    }
}
