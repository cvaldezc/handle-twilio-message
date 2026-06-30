package com.ib.poc.whatsapp.application.usecase;

import com.ib.poc.whatsapp.domain.model.OutboundMessage;
import com.ib.poc.whatsapp.application.port.in.SendMessageUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Application service for outbound WhatsApp messages.
 * Pure orchestration: accepts primitive params, builds domain model, delegates to use case.
 * No infrastructure dependencies.
 */
@Service
public class WhatsAppSendApplicationService {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppSendApplicationService.class);

    private final SendMessageUseCase sendMessageUseCase;

    public WhatsAppSendApplicationService(SendMessageUseCase sendMessageUseCase) {
        this.sendMessageUseCase = sendMessageUseCase;
    }

    /**
     * Sends an outbound WhatsApp message.
     *
     * @return Twilio message SID
     */
    public String send(String to, String body) {
        log.debug("WhatsAppSendApplicationService.send. To={}", to);
        OutboundMessage message = OutboundMessage.builder().to(to).body(body).build();
        return sendMessageUseCase.send(message);
    }
}
