package com.example.BigProject_25.model;

import java.time.LocalDateTime;

public class TaxiRequest {

    private LocalDateTime time;
    private int callNum;
    private int peopleNum;
    private int taxiNum;
    private String mode;
    private int pendingTaxis;
    private int totalCallCount;

    // Getters and Setters
    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public int getCallNum() {
        return callNum;
    }

    public void setCallNum(int callNum) {
        this.callNum = callNum;
    }

    public int getPeopleNum() {
        return peopleNum;
    }

    public void setPeopleNum(int peopleNum) {
        this.peopleNum = peopleNum;
    }

    public int getTaxiNum() {
        return taxiNum;
    }

    public void setTaxiNum(int taxiNum) {
        this.taxiNum = taxiNum;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getPendingTaxis() {
        return pendingTaxis;
    }

    public void setPendingTaxis(int pendingTaxis) {
        this.pendingTaxis = pendingTaxis;
    }

    public int getTotalCallCount() {
        return totalCallCount;
    }

    public void setTotalCallCount(int totalCallCount) {
        this.totalCallCount = totalCallCount;
    }
}
