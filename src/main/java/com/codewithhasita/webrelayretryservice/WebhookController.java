package com.codewithhasita.webrelayretryservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

@RestController
public class WebhookController {
    private final EventRepository eventRepository;
    private final RetryService retryService;
    private final ReceiverRepository receiverRepository;

    public WebhookController(EventRepository eventRepository, ReceiverRepository receiverRepository,
                             RetryService retryService){
        this.eventRepository = eventRepository;
        this.receiverRepository = receiverRepository;
        this.retryService = retryService;
    }

    @PostMapping("${webhook-api.path}")
    public String receive(@RequestBody String payload){
        Receiver receiver = receiverRepository.findAll().stream()
                .findFirst().orElseThrow(() -> new RuntimeException("No test receiver configured"));

        Event e = new Event();
        e.setPayload(payload);
        e.setStatus(Status.PENDING);
        e.setReceiver(receiver);
        e.setReceivedAt(LocalDateTime.now());
        e.setAttemptCount(0);
        eventRepository.save(e);

        retryService.attemptDelivery(e);

        return "Received and sent!";
    }
}
