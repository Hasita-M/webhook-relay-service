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
    private EventRepository eventRepository;
    private DeliveryRepository deliveryRepository;
    private RestClient client;
    private ReceiverRepository receiverRepository;
    @Value("${receiver-api.base-url}")
    private String receiverBaseUrl;

    @Value("${receiver-api.path}")
    private String receiverPath;

    public WebhookController(EventRepository eventRepository, ReceiverRepository receiverRepository,
                             DeliveryRepository deliveryRepository){
        this.eventRepository = eventRepository;
        this.receiverRepository = receiverRepository;
        this.deliveryRepository = deliveryRepository;
        this.client = RestClient.create();
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

        String message = "Received";

        Delivery d = new Delivery();
        d.setEvent(e);
        d.setAttemptNumber(1);
        d.setAttemptedAt(LocalDateTime.now());

        try {
            ResponseEntity<Void> response = client.post()
                    .uri(receiver.getDestinationURL())
                    .body(e.getPayload())
                    .retrieve()
                    .toBodilessEntity();

            e.setStatus(Status.SUCCESS);
            d.setStatusCode(response.getStatusCode().value());
            d.setSuccess(true);
            message += " and Sent";
        } catch (Exception ex){
            e.setStatus(Status.FAILED);
            d.setStatusCode(null);
            d.setSuccess(false);
            d.setErrorMessage(ex.getMessage());
            System.err.println("Delivery failed: " + ex.getMessage());
        }
        e.setAttemptCount(e.getAttemptCount() + 1);
        eventRepository.save(e);
        deliveryRepository.save(d);
        return message;
    }
}
