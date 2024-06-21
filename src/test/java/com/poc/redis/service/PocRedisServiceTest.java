package com.poc.redis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PocRedisServiceTest {
    private static final String CLIENT_ID = "123456";

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private PocRedisService pocRedisService;

    @BeforeEach
    public void setup() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("Teste canMakeRequest - Retorna true quando o tempo desde a última requisição é maior que 50 minutos")
    void testCanMakeRequest_ReturnsTrueWhenTimeElapsed() {
        String lastRequestTime = Instant.now().minusSeconds(3600).toString();
        when(valueOperations.get("userId = " + CLIENT_ID)).thenReturn(lastRequestTime);

        boolean result = pocRedisService.canMakeRequest(CLIENT_ID);

        assertTrue(result);
        verify(valueOperations, times(1)).get("userId = " + CLIENT_ID);
    }


    @Test
    @DisplayName("Teste canMakeRequest - Retorna false quando o tempo desde a última requisição é menor que 50 minutos")
    void testCanMakeRequest_ReturnsFalseWhenTimeNotElapsed() {
        // Simulando que a última requisição foi feita há apenas 1 minuto (menos de 50 minutos)
        String lastRequestTime = Instant.now().minusSeconds(60).toString();
        when(valueOperations.get("userId = " + CLIENT_ID)).thenReturn(lastRequestTime);

        boolean result = pocRedisService.canMakeRequest(CLIENT_ID);

        assertFalse(result);
        verify(valueOperations, times(1)).get("userId = " + CLIENT_ID);
    }

    @Test
    @DisplayName("Teste canMakeRequest - Retorna true quando não há registro de última requisição")
    void testCanMakeRequest_ReturnsTrueWhenNoLastRequest() {
        when(valueOperations.get("userId = " + CLIENT_ID)).thenReturn(null);

        boolean result = pocRedisService.canMakeRequest(CLIENT_ID);

        assertTrue(result);
        verify(valueOperations, times(1)).get("userId = " + CLIENT_ID);
    }

    @Test
    @DisplayName("Teste updateRequestTime - Verifica se o método set é chamado corretamente")
    void testUpdateRequestTime() {
        pocRedisService.updateRequestTime(CLIENT_ID);

        verify(valueOperations, times(1)).set(eq("userId = " + CLIENT_ID), anyString(), eq(50L), eq(TimeUnit.MINUTES));
    }
}