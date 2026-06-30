package com.ib.poc.whatsapp.infrastructure.web.controller;

import com.ib.poc.whatsapp.application.usecase.WhatsAppWebhookApplicationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * HTTP adapter for inbound WhatsApp webhooks.
 * No business logic — binds HTTP params and delegates to WhatsAppWebhookApplicationService.
 *
 * Configure in Twilio Console:
 *   Messaging → Try it out → Send a WhatsApp message → Sandbox settings
 *   "When a message comes in" = https://<ngrok-url>/webhook/whatsapp
 *
 * NOTE (production): validate X-Twilio-Signature header with RequestValidator.
 */
@RestController
public class WhatsAppWebhookController {

    private final WhatsAppWebhookApplicationService webhookService;

    public WhatsAppWebhookController(WhatsAppWebhookApplicationService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping(value = "/webhook/whatsapp", produces = MediaType.TEXT_XML_VALUE)
    public ResponseEntity<String> receiveMessage(
            @RequestParam("From") String from,
            @RequestParam("To") String to,
            @RequestParam("Body") String body,
            @RequestParam("MessageSid") String messageSid,
            @RequestParam(value = "NumMedia", defaultValue = "0") int numMedia,
            @RequestParam(value = "ProfileName", required = false) String profileName,
            @RequestParam Map<String, String> allParams) {
        String twiml = webhookService.process(from, to, body, messageSid, numMedia, profileName, allParams);
        return ResponseEntity.ok().contentType(MediaType.TEXT_XML).body(twiml);
    }
}
