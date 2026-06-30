package com.ib.poc.whatsapp.application.usecase;

import com.ib.poc.whatsapp.domain.model.InboundMessage;
import com.ib.poc.whatsapp.domain.model.MediaAttachment;
import com.ib.poc.whatsapp.application.port.in.ReceiveMessageUseCase;
import com.ib.poc.whatsapp.application.port.out.MediaDownloaderPort;
import com.ib.poc.whatsapp.application.port.out.MediaStoragePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Optional;

@Service
public class ReceiveMessageService implements ReceiveMessageUseCase {

    private static final Logger log = LoggerFactory.getLogger(ReceiveMessageService.class);

    private final MediaDownloaderPort mediaDownloaderPort;
    private final MediaStoragePort mediaStoragePort;

    public ReceiveMessageService(MediaDownloaderPort mediaDownloaderPort,
                                 MediaStoragePort mediaStoragePort) {
        this.mediaDownloaderPort = mediaDownloaderPort;
        this.mediaStoragePort = mediaStoragePort;
    }

    @Override
    public Optional<String> handleInbound(InboundMessage message) {
        log.info("=== INBOUND WHATSAPP MESSAGE ===");
        log.info("  MessageSid  : {}", message.getMessageSid());
        log.info("  From        : {}", message.getFrom());
        log.info("  To          : {}", message.getTo());
        log.info("  ProfileName : {}", message.getProfileName());
        log.info("  Body        : {}", message.getBody());
        log.info("  NumMedia    : {}", message.getNumMedia());
        log.info("================================");

        if (message.getNumMedia() == 0) {
            log.info("No media attachments — no reply sent.");
            return Optional.empty();
        }

        log.info("Processing {} media attachment(s)...", message.getMediaAttachments().size());

        for (MediaAttachment attachment : message.getMediaAttachments()) {
            log.info("Downloading attachment. index={} contentType={}", attachment.getIndex(), attachment.getContentType());
            byte[] bytes = mediaDownloaderPort.download(attachment);
            Path saved = mediaStoragePort.store(attachment, message.getMessageSid(), bytes);
            log.info("Attachment saved. index={} path={}", attachment.getIndex(), saved);
        }

        log.info("All media processed. Replying 'Mensaje recibido'.");
        return Optional.of("Mensaje recibido");
    }
}
