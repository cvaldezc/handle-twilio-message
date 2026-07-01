package com.ib.poc.whatsapp.application.usecase;

import com.ib.poc.whatsapp.domain.model.InboundMessage;
import com.ib.poc.whatsapp.domain.model.MediaAttachment;
import com.ib.poc.whatsapp.application.port.in.ReceiveMessageUseCase;
import com.ib.poc.whatsapp.application.port.out.TwimlBuilderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Application service for inbound WhatsApp webhooks.
 * Pure orchestration: no Twilio SDK imports, no infrastructure dependencies.
 * Depends only on domain ports and domain models.
 */
@Service
public class WhatsAppWebhookApplicationService {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppWebhookApplicationService.class);
    private static final String WHATSAPP_PREFIX = "whatsapp:";

    private final ReceiveMessageUseCase receiveMessageUseCase;
    private final TwimlBuilderPort twimlBuilderPort;

    public WhatsAppWebhookApplicationService(ReceiveMessageUseCase receiveMessageUseCase,
                                             TwimlBuilderPort twimlBuilderPort) {
        this.receiveMessageUseCase = receiveMessageUseCase;
        this.twimlBuilderPort = twimlBuilderPort;
    }

    /**
     * Processes an inbound webhook request.
     *
     * @param allParams full map of Twilio form params (includes MediaUrl{N}, MediaContentType{N})
     * @return TwiML XML string ready to return as HTTP response
     */
    public String process(String from, String to, String body, String messageSid,
                          int numMedia, String profileName,
                          Map<String, String> allParams) {

        log.debug("WhatsAppWebhookApplicationService.process. MessageSid={} NumMedia={}", messageSid, numMedia);

        List<MediaAttachment> attachments = buildAttachments(numMedia, allParams);

        InboundMessage message = InboundMessage.builder()
                .from(stripPrefix(from))
                .to(stripPrefix(to))
                .body(body)
                .messageSid(messageSid)
                .numMedia(numMedia)
                .profileName(profileName)
                .mediaAttachments(attachments)
                .build();

        Optional<String> reply = receiveMessageUseCase.handleInbound(message);

        return twimlBuilderPort.build(reply.orElse(null));
    }

    private List<MediaAttachment> buildAttachments(int numMedia, Map<String, String> allParams) {
        return IntStream.range(0, numMedia)
                .filter(i -> allParams.containsKey("MediaUrl" + i))
                .mapToObj(i -> MediaAttachment.builder()
                        .index(i)
                        .url(allParams.get("MediaUrl" + i))
                        .contentType(allParams.getOrDefault("MediaContentType" + i, "application/octet-stream"))
                        .filename(allParams.get("MediaFilename" + i))
                        .build())
                .toList();
    }

    private String stripPrefix(String number) {
        if (number == null) return null;
        return number.startsWith(WHATSAPP_PREFIX) ? number.substring(WHATSAPP_PREFIX.length()) : number;
    }
}
