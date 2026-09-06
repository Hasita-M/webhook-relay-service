package com.codewithhasita.webrelayretryservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class ReceiverManagementController {
    private final ReceiverRepository receiverRepository;
    private final EventRepository eventRepository;
    private final DeliveryRepository deliveryRepository;

    @Value("${app.public-base-url}")
    private String baseUrl;

    @Value("${app.encryption-key}")
    private String encryptionKey;

    public ReceiverManagementController(ReceiverRepository receiverRepository,
                                        EventRepository eventRepository,
                                        DeliveryRepository deliveryRepository) {
        this.receiverRepository = receiverRepository;
        this.eventRepository = eventRepository;
        this.deliveryRepository = deliveryRepository;
    }

    @PostMapping("/receivers")
    public ResponseEntity<CreateReceiverResponse> create(@RequestBody CreateReceiverRequest request){
        Receiver r = new Receiver();
        r.setName(request.getName());
        r.setDestinationURL(request.getDestinationURL());
        String plainSecret = UUID.randomUUID().toString();
        r.setSecretKey(EncryptionUtil.encrypt(plainSecret, encryptionKey));
        r.setManagementToken(UUID.randomUUID().toString());

        receiverRepository.save(r);
        String webhookUrl = baseUrl + "/webhook/" + r.getReceiverId();
        String managementUrl = baseUrl + "/manage/" + r.getManagementToken();

        return ResponseEntity.ok(new CreateReceiverResponse(webhookUrl, managementUrl));
    }

    @GetMapping("/manage/{token}")
    public ResponseEntity<ReceiverDetailsResponse> view(@PathVariable("token") String token){
        Receiver receiver = receiverRepository.findByManagementToken(token)
                .orElseThrow(() -> new ReceiverNotFoundException("No receiver found for this management link"));

        String webhookUrl = baseUrl + "/webhook/" + receiver.getReceiverId();
        String plainSecret = EncryptionUtil.decrypt(receiver.getSecretKey(), encryptionKey);

        return ResponseEntity.ok(new ReceiverDetailsResponse(receiver.getName(),
                receiver.getDestinationURL(), webhookUrl, plainSecret, receiver.isChaosMode(), receiver.getMaxRetries()));
    }

    @PatchMapping("/manage/{token}")
    public ResponseEntity<ReceiverDetailsResponse> update(@PathVariable("token") String token,
                                                          @RequestBody UpdateReceiverRequest request){
        Receiver receiver = receiverRepository.findByManagementToken(token)
                .orElseThrow(() -> new ReceiverNotFoundException("No receiver found for this management link"));

        if (receiver.isTestReceiver()) {
            throw new ReceiverValidationException("This is a shared test receiver and cannot be edited.");
        }

        if (request.getName() != null) {
            receiver.setName(request.getName());
        }
        if (request.getDestinationUrl() != null) {
            receiver.setDestinationURL(request.getDestinationUrl());
        }
        if (request.getChaosMode() != null) {
            receiver.setChaosMode(request.getChaosMode());
        }
        if (request.getMaxRetries() != null) {
            if (request.getMaxRetries() < 1 || request.getMaxRetries() > 10) {
                throw new ReceiverValidationException("maxRetries must be between 1 and 10");
            }
            receiver.setMaxRetries(request.getMaxRetries());
        }

        receiverRepository.save(receiver);

        String webhookUrl = baseUrl + "/webhook/" + receiver.getReceiverId();
        String plainSecret = EncryptionUtil.decrypt(receiver.getSecretKey(), encryptionKey);

        return ResponseEntity.ok(new ReceiverDetailsResponse(receiver.getName(),
                receiver.getDestinationURL(), webhookUrl, plainSecret, receiver.isChaosMode(), receiver.getMaxRetries()));
    }

    @GetMapping("/manage/{token}/events")
    public ResponseEntity<List<EventWithDeliveriesResponse>> getEvents(@PathVariable("token") String token) {
        Receiver receiver = receiverRepository.findByManagementToken(token)
                .orElseThrow(() -> new ReceiverNotFoundException("No receiver found for this management link"));

        List<Event> events = eventRepository.findByReceiverOrderByReceivedAtDesc(receiver);

        List<EventWithDeliveriesResponse> result = new ArrayList<>();

        for (Event event : events) {
            List<Delivery> deliveries = deliveryRepository.findByEventOrderByAttemptNumberAsc(event);

            List<DeliveryAttemptResponse> attempts = new ArrayList<>();
            for (Delivery d : deliveries) {
                DeliveryAttemptResponse attemptResponse = new DeliveryAttemptResponse(
                        d.getAttemptNumber(), d.getAttemptedAt(), d.isSuccess(), d.getStatusCode(), d.getErrorMessage()
                );
                attempts.add(attemptResponse);
            }

            EventWithDeliveriesResponse eventResponse = new EventWithDeliveriesResponse(
                    event.getId(), event.getPayload(), event.getReceivedAt(), event.getStatus().toString(), event.getAttemptCount(), attempts
            );
            result.add(eventResponse);
        }

        return ResponseEntity.ok(result);
    }

    @ExceptionHandler(ReceiverNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ReceiverNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(ReceiverValidationException.class)
    public ResponseEntity<String> handleValidation(ReceiverValidationException ex) {
        return ResponseEntity.status(400).body(ex.getMessage());
    }
}