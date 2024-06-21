package com.poc.redis.controller;

import com.poc.redis.service.PocRedisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PocRedisController {

    private final PocRedisService pocRedisService;

    public PocRedisController(PocRedisService pocRedisService) {
        this.pocRedisService = pocRedisService;
    }

    @GetMapping("/request")
    public ResponseEntity<String> handleRequest(@RequestHeader("Client-Id") String clientId) {
        if (!pocRedisService.canMakeRequest(clientId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A requisição está em processamento");
        }

        pocRedisService.updateRequestTime(clientId);
        return ResponseEntity.ok("Requisição solicitada");
    }
}
