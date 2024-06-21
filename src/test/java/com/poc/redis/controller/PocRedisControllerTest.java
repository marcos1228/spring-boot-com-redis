package com.poc.redis.controller;

import com.poc.redis.service.PocRedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PocRedisController.class)
@ExtendWith(MockitoExtension.class)
public class PocRedisControllerTest {

    @MockBean
    private PocRedisService pocRedisService;

    @InjectMocks
    private PocRedisController pocRedis;

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("Teste handleRequest - Retorna 200 OK quando a requisição é permitida")
    void testHandleRequest_ReturnsOkWhenRequestAllowed() throws Exception {
        String clientId = "123456";
        when(pocRedisService.canMakeRequest(clientId)).thenReturn(true);

        mockMvc.perform(get("/request")
                        .header("Client-Id", clientId))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals("Requisição solicitada", result.getResponse().getContentAsString()));
    }

    @Test
    @DisplayName("Teste handleRequest - Retorna 409 Conflict quando a requisição não é permitida")
    void testHandleRequest_ReturnsConflictWhenRequestNotAllowed() throws Exception {
        String clientId = "123456";
        when(pocRedisService.canMakeRequest(clientId)).thenReturn(false);

        mockMvc.perform(get("/request")
                        .header("Client-Id", clientId))
                .andExpect(status().isConflict())
                .andExpect(result -> assertEquals("A requisição está em processamento", result.getResponse().getContentAsString()));
    }
}
