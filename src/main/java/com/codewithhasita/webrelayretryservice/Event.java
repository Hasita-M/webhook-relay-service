package com.codewithhasita.webrelayretryservice;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "event")
public class Event {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String payload;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime receivedAt;

    private String deliveryURL;

    public Event(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getDeliveryURL() {
        return deliveryURL;
    }

    public void setDeliveryURL(String deliveryURL) {
        this.deliveryURL = deliveryURL;
    }
}
