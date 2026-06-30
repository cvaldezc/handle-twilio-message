package com.ib.poc.whatsapp.application.port.in;

import com.ib.poc.whatsapp.domain.model.InboundMessage;

import java.util.Optional;

public interface ReceiveMessageUseCase {

    /**
     * Processes an inbound WhatsApp message.
     *
     * @return Optional with reply text when media was present, empty when no media (no reply sent)
     */
    Optional<String> handleInbound(InboundMessage message);
}
