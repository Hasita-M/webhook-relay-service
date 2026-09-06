package com.codewithhasita.webrelayretryservice;

public class UpdateReceiverRequest {
    private String name;
    private String destinationUrl;
    private Boolean chaosMode;
    private Integer maxRetries;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDestinationUrl() {
        return destinationUrl;
    }

    public void setDestinationUrl(String destinationUrl) {
        this.destinationUrl = destinationUrl;
    }

    public Boolean getChaosMode() {
        return chaosMode;
    }

    public void setChaosMode(Boolean chaosMode) {
        this.chaosMode = chaosMode;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }
}
