package com.codewithhasita.webrelayretryservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
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

    @PostMapping("${webhook-api.path}")
        public ResponseEntity<String> receive(@RequestBody String payload,
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

        Receiver receiver = receiverRepository.findAll().stream()
                .findFirst().orElseThrow(() -> new RuntimeException("No test receiver configured"));

        Event e = new Event();
        e.setPayload(payload);
        e.setStatus(Status.PENDING);
        e.setReceiver(receiver);
        e.setReceivedAt(LocalDateTime.now());
        e.setAttemptCount(0);
        e.setIdempotencyKey(idempotencyKey);

        try {
            eventRepository.save(e);
        } catch (DataIntegrityViolationException dup) {
            return ResponseEntity.ok("Duplicate event (race), already processed");
        }

        retryService.attemptDelivery(e);

        return ResponseEntity.ok("Received and Sent");
    }
}
