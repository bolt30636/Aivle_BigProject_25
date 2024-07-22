package com.example.BigProject_25.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OAuth2Controller {

    @GetMapping("/oauth2/callback/kakao")
    public String handleKakaoCallback(@AuthenticationPrincipal OAuth2User principal) {
        // principal 객체를 통해 사용자 정보를 얻을 수 있습니다.
        // 예: principal.getAttributes().get("id");
        return "redirect:/"; // 인증 후 리디렉션할 페이지
    }

    @GetMapping("/oauth2/callback/google")
    public String handleGoogleCallback(@AuthenticationPrincipal OAuth2User principal) {
        // principal 객체를 통해 사용자 정보를 얻을 수 있습니다.
        // 예: principal.getAttributes().get("sub");
        return "redirect:/"; // 인증 후 리디렉션할 페이지
    }
}
