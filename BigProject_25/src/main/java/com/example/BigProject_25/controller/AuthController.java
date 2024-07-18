package com.example.BigProject_25.controller;

import com.example.BigProject_25.model.User;
import com.example.BigProject_25.service.AuthService;
import com.example.BigProject_25.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String userID, @RequestParam String password, @RequestParam String captchaResponse) {
        if (authService.login(userID, password, captchaResponse)) {
            return ResponseEntity.ok("로그인 성공");
        } else {
            User user = userRepository.findByUserID(userID);
            if (user != null && user.isAccountLocked()) {
                return ResponseEntity.status(401).body("로그인 실패: 계정이 잠겼습니다.");
            } else {
                return ResponseEntity.status(401).body("로그인 실패: 사용자 ID 또는 비밀번호가 잘못되었습니다.");
            }
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> payload) {
        log.info(payload.toString());
        String name = payload.get("name");
        String email = payload.get("email");
        String userID = payload.get("userID");
        String phoneNumStr = payload.get("phoneNum");
        String password = payload.get("password");
        String captchaResponse = payload.get("captchaResponse");

        if (authService.signup(name, email, userID, phoneNumStr, password, captchaResponse)) {
            return ResponseEntity.ok("회원가입 성공. 이메일을 확인하세요.");
        } else {
            return ResponseEntity.status(400).body("회원가입 실패");
        }
    }
//
//    @PostMapping("/send-verification-email")
//    public ResponseEntity<String> sendVerificationEmail(@RequestParam String email) {
//        User user = new User();
//        user.setEmail(email);
//
//        // 사용자 저장
//        userRepository.save(user);
//
//        // 이메일 인증 토큰 생성 및 저장
//        String token = authService.generateVerificationToken(user);
//        authService.sendVerificationEmail(user, token);
//        return ResponseEntity.ok("인증 이메일이 전송되었습니다.");
//    }

    @PostMapping("/social-login")
    public ResponseEntity<?> socialLogin(@RequestParam String provider, @RequestParam String token) {
        if (authService.socialLogin(provider, token)) {
            return ResponseEntity.ok("소셜 로그인 성공");
        } else {
            return ResponseEntity.status(401).body("소셜 로그인 실패");
        }
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
//
//    @GetMapping("/confirm")
//    public ResponseEntity<String> confirmUser(@RequestParam("token") String token) {
//        boolean isConfirmed = authService.verifyEmail(token);
//        if (isConfirmed) {
//            return ResponseEntity.ok("사용자 인증 성공");
//        } else {
//            return ResponseEntity.status(400).body("유효하지 않거나 만료된 토큰입니다.");
//        }
//    }
}

