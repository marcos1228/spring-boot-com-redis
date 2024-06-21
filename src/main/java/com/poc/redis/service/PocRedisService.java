package com.poc.redis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class PocRedisService {

    private static final String REQUEST_PREFIX = "userId = ";


    private final StringRedisTemplate redisTemplate;

    public PocRedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public boolean canMakeRequest(String clientId) {
        log.info("ClientId: {}", clientId);

        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        log.info("Conexão com o Redis: {}", operations);
        String key = REQUEST_PREFIX + clientId;

        String lastRequestTime = operations.get(key);

        log.info("ClientId salvo no redis {}", key);
        if (lastRequestTime == null) {
            return true;
        }

        Instant lastRequest = Instant.parse(lastRequestTime);
        Instant now = Instant.now();
        Duration duration = Duration.between(lastRequest, now);

        return duration.toMinutes() >= 50;
    }

    public void updateRequestTime(String clientId) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String key = REQUEST_PREFIX + clientId;
        operations.set(key, Instant.now().toString(), 50, TimeUnit.MINUTES);
    }
}
