package com.example.BigProject_25.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ChatBotService {
    private final WebClient webClient;

    public ChatBotService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:5000").build();  // Flask 서버의 URL로 설정
    }

    public Mono<String> getChatbotResponse(String question) {
        System.out.println("Sending question: " + question);  // 보낸 요청 데이터를 로그로 출력
        return this.webClient.post()
                .uri("/chat")
                .header("Content-Type", "application/json")
                .bodyValue("{\"question\": \"" + question + "\"}")
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> getFlightStatus(int page, int perPage) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/flight-status")
                        .queryParam("page", page)
                        .queryParam("per_page", perPage)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> getParkingFees() {
        return this.webClient.get()
                .uri("/parking-fees")
                .retrieve()
                .bodyToMono(String.class);
    }
}
