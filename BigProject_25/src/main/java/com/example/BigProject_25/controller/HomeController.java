package com.example.BigProject_25.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Value("${kakao.rest-api-key}")
    private String kakaoRestApiKey;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${google.client-id}")
    private String googleClientId;

    @Value("${google.redirect-uri}")
    private String googleRedirectUri;

//    @Value("${naver.client-id}")
//    private String naverClientId;
//
//    @Value("${naver.redirect-uri}")
//    private String naverRedirectUri;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("kakaoRestApiKey", kakaoRestApiKey);
        model.addAttribute("kakaoRedirectUri", kakaoRedirectUri);
        model.addAttribute("googleClientId", googleClientId);
        model.addAttribute("googleRedirectUri", googleRedirectUri);
//        model.addAttribute("naverClientId", naverClientId);
//        model.addAttribute("naverRedirectUri", naverRedirectUri);
        return "user-management";  // 템플릿 파일명 (user-management.html)
    }
}
