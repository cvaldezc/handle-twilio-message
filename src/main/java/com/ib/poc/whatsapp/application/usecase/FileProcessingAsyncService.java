package com.ib.poc.whatsapp.application.usecase;

import com.ib.poc.whatsapp.application.port.out.ConversationSessionPort;
import com.ib.poc.whatsapp.application.port.out.MessageSenderPort;
import com.ib.poc.whatsapp.domain.model.ConversationSession;
import com.ib.poc.whatsapp.domain.model.OutboundMessage;
import com.ib.poc.whatsapp.domain.model.ReceivedFile;
import com.ib.poc.whatsapp.domain.model.SessionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FileProcessingAsyncService {

    private static final Logger log = LoggerFactory.getLogger(FileProcessingAsyncService.class);

    @Value("${conversation.processing-simulation-ms:5000}")
    private long processingSimMs;

    @Value("${conversation.session-completed-timeout-ms:20000}")
    private long sessionTimeoutMs;

    private final ConversationSessionPort sessionPort;
    private final MessageSenderPort messageSenderPort;

    public FileProcessingAsyncService(ConversationSessionPort sessionPort,
                                      MessageSenderPort messageSenderPort) {
        this.sessionPort = sessionPort;
        this.messageSenderPort = messageSenderPort;
    }

    @Async
    public void startProcessing(String phoneNumber) {
        log.info("Async processing started. phone={}", phoneNumber);

        ConversationSession session = sessionPort.findByPhone(phoneNumber).orElse(null);
        if (session == null || session.getState() != SessionState.COLLECTING_FILES) {
            log.warn("Skipping startProcessing — invalid state. phone={} state={}",
                    phoneNumber, session == null ? "null" : session.getState());
            return;
        }

        // 1. Transition to PROCESSING + notify user
        session.setState(SessionState.PROCESSING);
        session.setUpdatedAt(LocalDateTime.now());
        sessionPort.save(session);

        int total = session.getReceivedFiles().size();
        messageSenderPort.send(OutboundMessage.builder()
                .to(phoneNumber)
                .body("Recibidos " + total + " archivo(s). Iniciando procesamiento...")
                .build());
        log.info("Processing started. phone={} files={}", phoneNumber, total);

        // 2. Process each file individually
        for (ReceivedFile file : session.getReceivedFiles()) {
            try {
                Thread.sleep(processingSimMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Processing interrupted. phone={} file={}", phoneNumber, file.getFilename());
                return;
            }
            messageSenderPort.send(OutboundMessage.builder()
                    .to(phoneNumber)
                    .body("Archivo procesado: " + file.getFilename() + " | ID: " + file.getProcessingId())
                    .build());
            log.info("File result sent. phone={} filename={} processingId={}",
                    phoneNumber, file.getFilename(), file.getProcessingId());
        }

        // 3. Mark COMPLETED
        session.setState(SessionState.COMPLETED);
        session.setUpdatedAt(LocalDateTime.now());
        sessionPort.save(session);
        log.info("Processing completed. phone={}", phoneNumber);

        // 4. Session expiry window
        try {
            Thread.sleep(sessionTimeoutMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        // 5. If user did nothing during expiry window → notify + delete
        ConversationSession current = sessionPort.findByPhone(phoneNumber).orElse(null);
        if (current != null && current.getState() == SessionState.COMPLETED) {
            messageSenderPort.send(OutboundMessage.builder()
                    .to(phoneNumber)
                    .body("Sesión expirada. Envíe cualquier mensaje para iniciar el proceso nuevamente.")
                    .build());
            sessionPort.delete(phoneNumber);
            log.info("Session expired and deleted. phone={}", phoneNumber);
        }
    }
}
