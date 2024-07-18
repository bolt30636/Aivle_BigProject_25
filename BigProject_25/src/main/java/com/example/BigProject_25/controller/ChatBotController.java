package com.example.BigProject_25.controller;

import com.example.BigProject_25.service.ChatBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class ChatBotController {

    @Autowired
    private ChatBotService chatbotService;

    @PostMapping("/ask")
    public Mono<String> askQuestion(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        return chatbotService.getChatbotResponse(question);
    }

    @GetMapping("/flight-status")
    public Mono<String> getFlightStatus(@RequestParam int page, @RequestParam int perPage) {
        return chatbotService.getFlightStatus(page, perPage);
    }

    @GetMapping("/parking-fees")
    public Mono<String> getParkingFees() {
        return chatbotService.getParkingFees();
    }
}



//http://localhost:8080/ask
//http://localhost:8080/flight-status?page=1&perPage=10
//http://localhost:8080/parking-fees