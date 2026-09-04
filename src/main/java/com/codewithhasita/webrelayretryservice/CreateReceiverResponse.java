package com.codewithhasita.webrelayretryservice;

public class CreateReceiverResponse {
    private String webhookUrl;
    private String managementUrl;

    public CreateReceiverResponse(String webhookUrl, String managementUrl) {
        this.webhookUrl = webhookUrl;
        this.managementUrl = managementUrl;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public String getManagementUrl() {
        return managementUrl;
    }
}
