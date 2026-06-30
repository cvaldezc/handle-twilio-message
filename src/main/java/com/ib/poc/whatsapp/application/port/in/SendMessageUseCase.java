package com.ib.poc.whatsapp.application.port.in;

import com.ib.poc.whatsapp.domain.model.OutboundMessage;

public interface SendMessageUseCase {

    /**
     * Sends an outbound WhatsApp message.
     *
     * @return provider message SID
     */
    String send(OutboundMessage message);
}
