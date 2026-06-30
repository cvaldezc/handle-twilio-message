package com.ib.poc.whatsapp.infrastructure.adapter.twilio;

import com.ib.poc.whatsapp.domain.model.OutboundMessage;
import com.ib.poc.whatsapp.application.port.out.MessageSenderPort;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TwilioMessageSenderAdapter implements MessageSenderPort {

    private static final Logger log = LoggerFactory.getLogger(TwilioMessageSenderAdapter.class);
    private static final String WHATSAPP_PREFIX = "whatsapp:";

    @Value("${twilio.whatsapp.from}")
    private String fromNumber;

    @Override
    public String send(OutboundMessage outboundMessage) {
        String toWhatsApp = outboundMessage.getTo().startsWith(WHATSAPP_PREFIX)
                ? outboundMessage.getTo()
                : WHATSAPP_PREFIX + outboundMessage.getTo();

        log.info("=== OUTBOUND WHATSAPP SEND ===");
        log.info("  From    : {}", fromNumber);
        log.info("  To      : {}", toWhatsApp);
        log.info("  Message : {}", outboundMessage.getBody());
        log.info("==============================");
        log.debug("Calling Twilio REST API...");

        long start = System.currentTimeMillis();
        Message message = Message.creator(
                new PhoneNumber(toWhatsApp),
                new PhoneNumber(fromNumber),
                outboundMessage.getBody()
        ).create();
        long duration = System.currentTimeMillis() - start;

        log.info("Twilio API response. SID={} Status={} ErrorCode={} Duration={}ms",
                message.getSid(), message.getStatus(), message.getErrorCode(), duration);

        return message.getSid();
    }
}
