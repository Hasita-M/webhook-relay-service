package com.codewithhasita.webrelayretryservice;

import java.time.LocalDateTime;
import java.util.List;

public class EventWithDeliveriesResponse {
    private Long eventId;
    private String payload;
    private LocalDateTime receivedAt;
    private String status;
    private int attemptCount;
    private List<DeliveryAttemptResponse> attempts;

    public EventWithDeliveriesResponse(Long eventId, String payload, LocalDateTime receivedAt, String status, int attemptCount, List<DeliveryAttemptResponse> attempts) {
        this.eventId = eventId;
        this.payload = payload;
        this.receivedAt = receivedAt;
        this.status = status;
        this.attemptCount = attemptCount;
        this.attempts = attempts;
    }

    public Long getEventId() { return eventId; }
    public String getPayload() { return payload; }
    public LocalDateTime getReceivedAt() { return receivedAt; }
    public String getStatus() { return status; }
    public int getAttemptCount() { return attemptCount; }
    public List<DeliveryAttemptResponse> getAttempts() { return attempts; }
}