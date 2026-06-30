package com.ib.poc.whatsapp.infrastructure.web.controller;

import com.ib.poc.whatsapp.application.usecase.WhatsAppSendApplicationService;
import com.ib.poc.whatsapp.infrastructure.web.dto.SendMessageRequestDto;
import com.ib.poc.whatsapp.infrastructure.web.dto.SendMessageResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP adapter for outbound WhatsApp messages.
 * No business logic — binds HTTP request and delegates to WhatsAppSendApplicationService.
 *
 * POST /api/whatsapp/send
 * Body: { "to": "+521234567890", "message": "Hello!" }
 */
@RestController
public class WhatsAppSendController {

    private final WhatsAppSendApplicationService sendService;

    public WhatsAppSendController(WhatsAppSendApplicationService sendService) {
        this.sendService = sendService;
    }

    @PostMapping("/api/whatsapp/send")
    public ResponseEntity<SendMessageResponseDto> sendMessage(@RequestBody SendMessageRequestDto request) {
        String sid = sendService.send(request.getTo(), request.getMessage());
        return ResponseEntity.ok(SendMessageResponseDto.builder().messageSid(sid).status("sent").build());
    }
}
