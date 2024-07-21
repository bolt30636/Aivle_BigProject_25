package com.example.BigProject_25.service;

import com.example.BigProject_25.model.TaxiRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TaxiRequestService {

    private static final Logger logger = LoggerFactory.getLogger(TaxiRequestService.class);
    private Queue<Integer> pendingTaxisQueue = new LinkedList<>();
    private Queue<Long> pendingTaxisTimeQueue = new LinkedList<>();
    private List<TaxiRequest> taxiRequests = new ArrayList<>();
    private final RestTemplate restTemplate;
    private final String flaskDataUrl = "http://127.0.0.1:5002/flask-data";

    private boolean aiDetectionEnabled = true;
    private int lastPeopleNum = 0;
    private int stationedTaxis = 0;
    private int currentCallNum = 0;

    @Autowired
    public TaxiRequestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public int calculateCallNum(int peopleNum, int stationedTaxis, int pendingTaxis) {
        return Math.max(0, (peopleNum - 2 * (stationedTaxis + pendingTaxis)) / 2);
    }

    public void updatePendingTaxis() {
        long currentTime = System.currentTimeMillis();
        while (!pendingTaxisTimeQueue.isEmpty() && (currentTime - pendingTaxisTimeQueue.peek() > 300000)) { // 5분(300초) 동안 유지
            pendingTaxisQueue.poll();
            pendingTaxisTimeQueue.poll();
            logger.info("Removed pending taxi after 5 minutes.");
        }
    }

    public int getPendingTaxis() {
        updatePendingTaxis();
        int pendingCount = pendingTaxisQueue.stream().mapToInt(Integer::intValue).sum();
        logger.info("Current pending taxis count: {}", pendingCount);
        return pendingCount;
    }

    @Scheduled(fixedRate = 10000)
    public void logTaxiRequestPeriodically() {
        if (!aiDetectionEnabled) {
            return;
        }

        Map<String, Integer> result = fetchDataFromFlask();
        int peopleNum = result.get("peopleCount");
        int taxiNum = result.get("taxiCount");
        lastPeopleNum = peopleNum;
        stationedTaxis = taxiNum;
        int pendingTaxis = getPendingTaxis();

        currentCallNum = calculateCallNum(peopleNum, stationedTaxis, pendingTaxis);

        // 로그 출력
        String logMessage = "시간: " + LocalDateTime.now() +
                ", 승강장에 있는 택시수: " + stationedTaxis +
                ", 승강장에 있는 사람 수: " + peopleNum +
                ", 새로운 호출 수: " + currentCallNum +
                ", 오고 있는 택시 수: " + pendingTaxis +
                ", 총 호출 수: " + (currentCallNum + pendingTaxis);
        System.out.println(logMessage);

        if (currentCallNum > 0) {
            pendingTaxisQueue.add(currentCallNum);
            pendingTaxisTimeQueue.add(System.currentTimeMillis());
        }

        TaxiRequest request = new TaxiRequest();
        request.setPeopleNum(peopleNum);
        request.setTaxiNum(taxiNum);
        request.setCallNum(currentCallNum);
        request.setPendingTaxis(pendingTaxis);
        request.setTotalCallCount(currentCallNum + pendingTaxis);
        request.setTime(LocalDateTime.now());

        taxiRequests.add(request);
    }

    @Scheduled(fixedRate = 30000)
    public void detectAndLogTaxiRequests() {
        if (aiDetectionEnabled) {
            int pendingTaxis = getPendingTaxis();
            currentCallNum = calculateCallNum(lastPeopleNum, stationedTaxis, pendingTaxis);

            if (currentCallNum > 0) {
                System.out.println(currentCallNum + "대 호출하세요!");
            }

            if (currentCallNum > 0) {
                pendingTaxisQueue.add(currentCallNum);
                pendingTaxisTimeQueue.add(System.currentTimeMillis());
            }
        }

        updatePendingTaxis();
    }

    public void stopAIDetection() {
        aiDetectionEnabled = false;
        currentCallNum = 0;
        logAIStatus();
    }

    public void resumeAIDetection() {
        aiDetectionEnabled = true;
        logAIStatus();
    }

    public boolean isAIDetectionEnabled() {
        return aiDetectionEnabled;
    }

    private void logAIStatus() {
        String status = aiDetectionEnabled ? "resumed" : "stopped";
        System.out.println("AI detection " + status + " at " + LocalDateTime.now());
    }

    public Map<String, Integer> fetchDataFromFlask() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(flaskDataUrl, Map.class);
            return (Map<String, Integer>) response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("peopleCount", 0, "taxiCount", 0);
        }
    }

    public Map<String, Integer> getDetectionResult() {
        Map<String, Integer> detectionResult = new HashMap<>();
        detectionResult.put("peopleCount", lastPeopleNum);
        detectionResult.put("taxiCount", stationedTaxis);
        return detectionResult;
    }

    public Map<String, Object> getFlaskData() {
        Map<String, Object> data = new HashMap<>();
        data.put("aiStatus", aiDetectionEnabled ? "running" : "stopped");
        data.put("peopleCount", lastPeopleNum);
        data.put("taxiCount", stationedTaxis);
        data.put("callCount", currentCallNum);
        data.put("pendingTaxis", getPendingTaxis());
        data.put("totalCalls", currentCallNum + getPendingTaxis());
        return data;
    }

    public void updateDetectionResult(int peopleCount, int taxiCount) {
        lastPeopleNum = peopleCount;
        stationedTaxis = taxiCount;
        currentCallNum = calculateCallNum(peopleCount, stationedTaxis, getPendingTaxis());
    }

    public List<TaxiRequest> getAllRequests() {
        return taxiRequests;
    }

    // Getter 메소드 추가
    public int getLastPeopleCount() {
        return lastPeopleNum;
    }

    public int getLastTaxiCount() {
        return stationedTaxis;
    }

    public int getCurrentCallNum() {
        return currentCallNum;
    }
}
