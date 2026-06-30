package com.ib.poc.whatsapp.application.usecase;

import com.ib.poc.whatsapp.domain.model.OutboundMessage;
import com.ib.poc.whatsapp.application.port.in.SendMessageUseCase;
import com.ib.poc.whatsapp.application.port.out.MessageSenderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SendMessageService implements SendMessageUseCase {

    private static final Logger log = LoggerFactory.getLogger(SendMessageService.class);

    private final MessageSenderPort messageSenderPort;

    public SendMessageService(MessageSenderPort messageSenderPort) {
        this.messageSenderPort = messageSenderPort;
    }

    @Override
    public String send(OutboundMessage message) {
        log.info("Send use case invoked. To={} Body={}", message.getTo(), message.getBody());
        String sid = messageSenderPort.send(message);
        log.info("Send use case completed. SID={}", sid);
        return sid;
    }
}
