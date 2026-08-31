package com.codewithhasita.webrelayretryservice;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class RetryService {
    private static final int MAX_ATTEMPTS = 5;
    private static final long BASE_DELAY_SECONDS = 2;
    private static final long MAX_DELAY_SECONDS = 60;
    private static final String QUEUE_KEY = "retry_queue";

    private final EventRepository eventRepository;
    private final DeliveryRepository deliveryRepository;
    private final RestClient client;
    private final StringRedisTemplate redisTemplate;

    public RetryService(EventRepository eventRepository, DeliveryRepository deliveryRepository, StringRedisTemplate redisTemplate) {
        this.eventRepository = eventRepository;
        this.deliveryRepository = deliveryRepository;
        this.redisTemplate = redisTemplate;
        this.client = RestClient.create();
    }

    public void attemptDelivery(Event event){
        Receiver receiver = event.getReceiver();
        int attemptNumber = event.getAttemptCount() + 1;

        Delivery d = new Delivery();
        d.setEvent(event);
        d.setAttemptNumber(attemptNumber);
        d.setAttemptedAt(LocalDateTime.now());

        try{
            String signature = HmacUtil.sign(event.getPayload(), receiver.getSecretKey());
            ResponseEntity<Void> response = client.post().uri(receiver.getDestinationURL())
                    .header("X-Webhook-Signature", signature)
                    .body(event.getPayload()).retrieve().toBodilessEntity();
            d.setStatusCode(response.getStatusCode().value());
            d.setSuccess(true);
            event.setStatus(Status.SUCCESS);
            redisTemplate.opsForZSet().remove(QUEUE_KEY, event.getId().toString());
        } catch (Exception ex){
            d.setStatusCode(null);
            d.setSuccess(false);
            d.setErrorMessage(ex.getMessage());

            if(attemptNumber >= MAX_ATTEMPTS){
                event.setStatus(Status.FAILED);
                redisTemplate.opsForZSet().remove(QUEUE_KEY, event.getId().toString());
            } else {
                long delaySeconds = Math.min(BASE_DELAY_SECONDS * (1L << (attemptNumber-1)), MAX_DELAY_SECONDS);
                double nextAttemptedScore = Instant.now().plusSeconds(delaySeconds).toEpochMilli();
                redisTemplate.opsForZSet().add(QUEUE_KEY, event.getId().toString(), nextAttemptedScore);
                event.setStatus(Status.PENDING);
            }
        }

        event.setAttemptCount(attemptNumber);
        eventRepository.save(event);
        deliveryRepository.save(d);
    }
}
