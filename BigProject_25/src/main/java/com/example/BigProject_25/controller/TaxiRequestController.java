package com.example.BigProject_25.controller;

import com.example.BigProject_25.model.TaxiRequest;
import com.example.BigProject_25.service.TaxiRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@RestController
@RequestMapping("/taxi-requests")
public class TaxiRequestController {

    @Autowired
    private TaxiRequestService taxiRequestService;

    private Queue<Integer> pendingTaxiQueue = new ConcurrentLinkedQueue<>();
    private int totalCallCount = 0;

    @PostMapping("/detection")
    public ResponseEntity<String> receiveDetectionData(@RequestBody Map<String, Integer> data) {
        int peopleCount = data.get("peopleCount");
        int taxiCount = data.get("taxiCount");

        int newCallCount = taxiRequestService.calculateCallNum(peopleCount, taxiCount, taxiRequestService.getPendingTaxis());

        pendingTaxiQueue.add(newCallCount);
        if (pendingTaxiQueue.size() > 10) {
            pendingTaxiQueue.poll();
        }

        totalCallCount += newCallCount;

        TaxiRequest request = new TaxiRequest();
        request.setPeopleNum(peopleCount);
        request.setTaxiNum(taxiCount);
        request.setCallNum(newCallCount);
        request.setPendingTaxis(taxiRequestService.getPendingTaxis());
        request.setTotalCallCount(totalCallCount);
        request.setTime(LocalDateTime.now());

        taxiRequestService.getAllRequests().add(request);

        return ResponseEntity.ok("Data received and processed");
    }

    @GetMapping("/flask-data")
    public ResponseEntity<Map<String, Object>> getFlaskData() {
        List<TaxiRequest> requests = taxiRequestService.getAllRequests();
        TaxiRequest latestRequest = requests.get(requests.size() - 1);

        Map<String, Object> response = new HashMap<>();
        response.put("aiStatus", taxiRequestService.isAIDetectionEnabled() ? "running" : "stopped");
        response.put("peopleCount", latestRequest.getPeopleNum());
        response.put("taxiCount", latestRequest.getTaxiNum());
        response.put("callCount", latestRequest.getCallNum());
        response.put("pendingTaxis", taxiRequestService.getPendingTaxis());
        response.put("totalCalls", latestRequest.getTotalCallCount());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stopAI() {
        taxiRequestService.stopAIDetection();
        pendingTaxiQueue.clear();
        return ResponseEntity.ok("AI stopped");
    }

    @PostMapping("/resume")
    public ResponseEntity<String> resumeAI() {
        taxiRequestService.resumeAIDetection();
        return ResponseEntity.ok("AI resumed");
    }
}
