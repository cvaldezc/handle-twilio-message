package com.ib.poc.whatsapp.infrastructure.adapter.twilio;

import com.ib.poc.whatsapp.application.port.out.TwimlBuilderPort;
import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class TwilioTwimlBuilderAdapter implements TwimlBuilderPort {

    @Override
    public String build(String replyText) {
        if (replyText == null || replyText.isBlank()) {
            return new MessagingResponse.Builder().build().toXml();
        }
        return new MessagingResponse.Builder()
                .message(new Message.Builder(replyText).build())
                .build()
                .toXml();
    }
}
