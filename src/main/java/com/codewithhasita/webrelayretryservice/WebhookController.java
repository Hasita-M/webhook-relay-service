package com.codewithhasita.webrelayretryservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

@RestController
public class WebhookController {
    private DBConnector db;
    private RestClient client;
    @Value("${receiver-api.base-url}")
    private String receiverBaseUrl;

    @Value("${receiver-api.path}")
    private String receiverPath;

    public WebhookController(DBConnector db){
        this.db = db;
        this.client = RestClient.create();
    }

    @PostMapping("${webhook-api.path}")
    public String receive(@RequestBody String payload){
        Event e = new Event();
        e.setPayload(payload);
        e.setStatus(Status.PENDING);
        e.setDeliveryURL(receiverBaseUrl + receiverPath);
        e.setReceivedAt(LocalDateTime.now());
        db.save(e);

        String message = "Received";

        try {
            client.post().uri(e.getDeliveryURL()).body(e.getPayload()).retrieve().toBodilessEntity();
            e.setStatus(Status.SUCCESS);
        } catch (Exception ex){
            e.setStatus(Status.FAILED);
            System.err.println("Delivery failed: " + ex.getMessage());
        }
        message += " and Sent";
        db.save(e);

        return message;
    }
}
