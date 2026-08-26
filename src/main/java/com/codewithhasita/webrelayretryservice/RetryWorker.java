package com.codewithhasita.webrelayretryservice;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;

//polling worker
@Component
public class RetryWorker {
    private static final String QUEUE_KEY = "retry_queue";
    private final StringRedisTemplate redisTemplate;
    private final EventRepository eventRepository;
    private final RetryService retryService;

    public RetryWorker(StringRedisTemplate redisTemplate, EventRepository eventRepository, RetryService retryService) {
        this.redisTemplate = redisTemplate;
        this.eventRepository = eventRepository;
        this.retryService = retryService;
    }

    @Scheduled(fixedDelay = 1000) //run each second i.e. every 1000 ms
    public void processRetries(){
        double now = Instant.now().toEpochMilli();
        Set<String> idsSet = redisTemplate.opsForZSet().rangeByScore(QUEUE_KEY, 0, now);

        if(idsSet == null) return;

        for(String idString: idsSet){
            Long eventId = Long.parseLong(idString);
            eventRepository.findByIdWithReceiver(eventId).ifPresent(retryService::attemptDelivery);
        }
    }
}
