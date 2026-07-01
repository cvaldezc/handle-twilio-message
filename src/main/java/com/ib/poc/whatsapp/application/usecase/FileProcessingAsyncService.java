package com.ib.poc.whatsapp.application.usecase;

import com.ib.poc.whatsapp.application.port.out.ConversationSessionPort;
import com.ib.poc.whatsapp.application.port.out.MessageSenderPort;
import com.ib.poc.whatsapp.domain.model.ConversationSession;
import com.ib.poc.whatsapp.domain.model.OutboundMessage;
import com.ib.poc.whatsapp.domain.model.ReceivedFile;
import com.ib.poc.whatsapp.domain.model.SessionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FileProcessingAsyncService {

    private static final Logger log = LoggerFactory.getLogger(FileProcessingAsyncService.class);
    private static final long SIMULATED_PROCESSING_MS = 5000;

    private final ConversationSessionPort sessionPort;
    private final MessageSenderPort messageSenderPort;

    public FileProcessingAsyncService(ConversationSessionPort sessionPort,
                                      MessageSenderPort messageSenderPort) {
        this.sessionPort = sessionPort;
        this.messageSenderPort = messageSenderPort;
    }

    @Async
    public void processFiles(String phoneNumber) {
        log.info("Async processing started. phone={}", phoneNumber);
        try {
            Thread.sleep(SIMULATED_PROCESSING_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Processing interrupted. phone={}", phoneNumber);
            return;
        }

        ConversationSession session = sessionPort.findByPhone(phoneNumber).orElse(null);
        if (session == null) {
            log.warn("Session not found after processing. phone={}", phoneNumber);
            return;
        }

        String reply = buildProcessingResultMessage(session);
        log.info("Sending processing result. phone={} files={}", phoneNumber, session.getReceivedFiles().size());

        messageSenderPort.send(OutboundMessage.builder()
                .to(phoneNumber)
                .body(reply)
                .build());

        session.setState(SessionState.COMPLETED);
        session.setUpdatedAt(LocalDateTime.now());
        sessionPort.save(session);
        log.info("Async processing completed. phone={}", phoneNumber);
    }

    private String buildProcessingResultMessage(ConversationSession session) {
        StringBuilder sb = new StringBuilder();
        sb.append("Procesamiento completado para ")
          .append(session.getCompanyInfo().getCompanyName())
          .append(" (CUIT: ").append(session.getCuit()).append(").\n\n");
        sb.append("Archivos procesados:\n");

        for (ReceivedFile file : session.getReceivedFiles()) {
            sb.append("- ").append(file.getFilename())
              .append(" | ID: ").append(file.getProcessingId()).append("\n");
        }

        return sb.toString();
    }
}
