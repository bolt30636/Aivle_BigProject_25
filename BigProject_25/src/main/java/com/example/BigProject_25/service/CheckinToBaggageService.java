package com.example.BigProject_25.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

@Service
public class CheckinToBaggageService {

    @Value("http://127.0.0.1:5000") // application.properties에서 Flask 서버 URL을 가져옴
    private String flaskServerUrl;

    public String getCheckinToBaggageTime() {
        RestTemplate restTemplate = new RestTemplate();
        String url = flaskServerUrl + "/predict/checkin-to-baggage";
        // Flask 서버에 GET 요청을 보내 예측 결과를 받아옴
        String response = restTemplate.getForObject(url, String.class);
        return response;
    }
}