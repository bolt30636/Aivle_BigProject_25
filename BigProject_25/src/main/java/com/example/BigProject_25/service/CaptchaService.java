package com.example.BigProject_25.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class CaptchaService {

    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    public boolean verifyCaptcha(String captchaResponse) {
        String url = "https://www.google.com/recaptcha/api/siteverify";
        Map<String, String> params = new HashMap<>();
        params.put("secret", recaptchaSecret);
        params.put("response", captchaResponse);

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.postForObject(url, params, Map.class);

        return (Boolean) response.get("success");
    }
}
