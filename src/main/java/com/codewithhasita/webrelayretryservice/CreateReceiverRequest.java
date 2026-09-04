package com.codewithhasita.webrelayretryservice;

public class CreateReceiverRequest {
    private String name;
    private String destinationURL;

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
}
