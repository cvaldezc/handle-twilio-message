package com.ib.poc.whatsapp.application.port.out;

import com.ib.poc.whatsapp.domain.model.OutboundMessage;

public interface MessageSenderPort {
    String send(OutboundMessage message);
}
