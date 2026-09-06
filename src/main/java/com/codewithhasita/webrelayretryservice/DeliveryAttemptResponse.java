package com.codewithhasita.webrelayretryservice;

import java.time.LocalDateTime;

public class DeliveryAttemptResponse {
    private int attemptNumber;
    private LocalDateTime attemptedAt;
    private boolean success;
    private Integer statusCode;
    private String errorMessage;

    public DeliveryAttemptResponse(int attemptNumber, LocalDateTime attemptedAt, boolean success, Integer statusCode, String errorMessage) {
        this.attemptNumber = attemptNumber;
        this.attemptedAt = attemptedAt;
        this.success = success;
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }

    public int getAttemptNumber() { return attemptNumber; }
    public LocalDateTime getAttemptedAt() { return attemptedAt; }
    public boolean isSuccess() { return success; }
    public Integer getStatusCode() { return statusCode; }
    public String getErrorMessage() { return errorMessage; }
}