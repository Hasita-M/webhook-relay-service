package com.codewithhasita.webrelayretryservice;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

@RestController
public class WebhookController {
    private DBConnector db;
    private RestClient client;

    public WebhookController(DBConnector db){
        this.db = db;
        this.client = RestClient.create();
    }

    @PostMapping("${webhook-api.path}")
    public String receive(@RequestBody String payload){
        Event e = new Event();
        e.setPayload(payload);
        e.setStatus(Status.PENDING);
        e.setDeliveryURL("http://localhost:8080/receiver"); //hardcoded for now
        //e.setDeliveryURL("http://localhost:9999/receiver"); to test failure
        e.setReceivedAt(LocalDateTime.now());
        db.save(e);

        String message = "Received";

        try {
            client.post().uri(e.getDeliveryURL()).body(e.getPayload()).retrieve().toBodilessEntity();
            e.setStatus(Status.SUCCESS);
        } catch (Exception ex){
            e.setStatus(Status.FAILED);
        }
        message += " and Sent";
        db.save(e);

        return message;
    }
}
