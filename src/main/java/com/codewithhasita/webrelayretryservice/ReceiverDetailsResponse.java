package com.codewithhasita.webrelayretryservice;

public class ReceiverDetailsResponse {
    private String name;
    private String destinationUrl;
    private String webhookUrl;
    private String secretKey;
    private boolean chaosMode;
    private int maxRetries;

    public ReceiverDetailsResponse(String name, String destinationUrl, String webhookUrl, String secretKey, boolean chaosMode, int maxRetries) {
        this.name = name;
        this.destinationUrl = destinationUrl;
        this.webhookUrl = webhookUrl;
        this.secretKey = secretKey;
        this.chaosMode = chaosMode;
        this.maxRetries = maxRetries;
    }

    public String getName() {
        return name;
    }

    public String getDestinationUrl() {
        return destinationUrl;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public boolean isChaosMode() {
        return chaosMode;
    }

    public int getMaxRetries() {
        return maxRetries;
    }
}
