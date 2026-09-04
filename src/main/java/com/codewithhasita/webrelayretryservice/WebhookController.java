package com.codewithhasita.webrelayretryservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
public class WebhookController {
    private final EventRepository eventRepository;
    private final RetryService retryService;
    private final ReceiverRepository receiverRepository;
    private final StringRedisTemplate redisTemplate;

    public WebhookController(EventRepository eventRepository, ReceiverRepository receiverRepository,
                             RetryService retryService, StringRedisTemplate redisTemplate){
        this.eventRepository = eventRepository;
        this.receiverRepository = receiverRepository;
        this.retryService = retryService;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("${webhook-api.path}/{receiverId}")
        public ResponseEntity<String> receive(@PathVariable Long receiverId, @RequestBody String payload,
                              @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey){

        if (idempotencyKey != null) {
            String redisKey = "idempotency:" + idempotencyKey;

            if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
                return ResponseEntity.ok("Duplicate event (cached)");
            }

            Optional<Event> existing = eventRepository.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                redisTemplate.opsForValue().set(redisKey, "1", Duration.ofHours(24));
                return ResponseEntity.ok("Duplicate event, original status: " + existing.get().getStatus());
            }
        }

        Receiver receiver = receiverRepository.findById(receiverId)
                .orElseThrow(() -> new ReceiverNotFoundException("No receiver found with id " + receiverId));

        Event e = new Event();
        e.setPayload(payload);
        e.setStatus(Status.PENDING);
        e.setReceiver(receiver);
        e.setReceivedAt(LocalDateTime.now());
        e.setAttemptCount(0);
        e.setIdempotencyKey(idempotencyKey);

        try {
            eventRepository.save(e);
            if (idempotencyKey != null) {
                redisTemplate.opsForValue().set("idempotency:" + idempotencyKey, "1", Duration.ofHours(24));
            }
        } catch (DataIntegrityViolationException dup) {
            return ResponseEntity.ok("Duplicate event (race), already processed");
        }

        retryService.attemptDelivery(e);

        return ResponseEntity.ok("Received and Sent");
    }

    @ExceptionHandler(ReceiverNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ReceiverNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}
