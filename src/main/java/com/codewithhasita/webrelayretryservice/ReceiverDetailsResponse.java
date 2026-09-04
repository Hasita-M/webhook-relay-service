package com.codewithhasita.webrelayretryservice;

public class ReceiverDetailsResponse {
    private String name;
    private String destinationUrl;
    private String webhookUrl;
    private String secretKey;

    public ReceiverDetailsResponse(String name, String destinationUrl, String webhookUrl, String secretKey) {
        this.name = name;
        this.destinationUrl = destinationUrl;
        this.webhookUrl = webhookUrl;
        this.secretKey = secretKey;
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
}
