package com.codewithhasita.webrelayretryservice;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "receiver")
public class Receiver {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long receiverId;

    private String name;

    private String destinationURL;

    private String secretKey;

    //user not required for creating receiver, handled by management token
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(name = "management_token", unique = true)
    private String managementToken;

    @Column(name = "chaos_mode")
    private boolean chaosMode = false;

    @Column(name = "max_retries")
    private int maxRetries = 5;

    public Receiver() {
    }

    public boolean isChaosMode() {
        return chaosMode;
    }

    public void setChaosMode(boolean chaosMode) {
        this.chaosMode = chaosMode;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public String getManagementToken() {
        return managementToken;
    }

    public void setManagementToken(String managementToken) {
        this.managementToken = managementToken;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDestinationURL() {
        return destinationURL;
    }

    public void setDestinationURL(String destinationURL) {
        this.destinationURL = destinationURL;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
