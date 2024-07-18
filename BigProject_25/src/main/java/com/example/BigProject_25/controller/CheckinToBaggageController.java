package com.example.BigProject_25.controller;

import com.example.BigProject_25.service.CheckinToBaggageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CheckinToBaggageController {

    @Autowired
    private CheckinToBaggageService checkinToBaggageService;

    @GetMapping("/checkin-to-baggage")
    public String getCheckinToBaggageTime() {
        // 서비스에서 예측 결과를 받아와 반환
        return checkinToBaggageService.getCheckinToBaggageTime();
    }
}
