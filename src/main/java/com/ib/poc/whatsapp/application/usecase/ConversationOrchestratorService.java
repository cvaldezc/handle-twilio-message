package com.ib.poc.whatsapp.application.usecase;

import com.ib.poc.whatsapp.application.port.in.ReceiveMessageUseCase;
import com.ib.poc.whatsapp.application.port.out.CompanyInfoPort;
import com.ib.poc.whatsapp.application.port.out.ConversationSessionPort;
import com.ib.poc.whatsapp.application.port.out.MediaDownloaderPort;
import com.ib.poc.whatsapp.application.port.out.MediaStoragePort;
import com.ib.poc.whatsapp.domain.model.CompanyInfo;
import com.ib.poc.whatsapp.domain.model.ConversationSession;
import com.ib.poc.whatsapp.domain.model.InboundMessage;
import com.ib.poc.whatsapp.domain.model.MediaAttachment;
import com.ib.poc.whatsapp.domain.model.ReceivedFile;
import com.ib.poc.whatsapp.domain.model.SessionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConversationOrchestratorService implements ReceiveMessageUseCase {

    private static final Logger log = LoggerFactory.getLogger(ConversationOrchestratorService.class);

    private final ConversationSessionPort sessionPort;
    private final CompanyInfoPort companyInfoPort;
    private final MediaDownloaderPort mediaDownloaderPort;
    private final MediaStoragePort mediaStoragePort;
    private final FileProcessingAsyncService fileProcessingAsyncService;

    public ConversationOrchestratorService(ConversationSessionPort sessionPort,
                                           CompanyInfoPort companyInfoPort,
                                           MediaDownloaderPort mediaDownloaderPort,
                                           MediaStoragePort mediaStoragePort,
                                           FileProcessingAsyncService fileProcessingAsyncService) {
        this.sessionPort = sessionPort;
        this.companyInfoPort = companyInfoPort;
        this.mediaDownloaderPort = mediaDownloaderPort;
        this.mediaStoragePort = mediaStoragePort;
        this.fileProcessingAsyncService = fileProcessingAsyncService;
    }

    @Override
    public Optional<String> handleInbound(InboundMessage message) {
        String phone = message.getFrom();
        log.info("Inbound message. phone={} numMedia={} body='{}'", phone, message.getNumMedia(), message.getBody());

        Optional<ConversationSession> existing = sessionPort.findByPhone(phone);

        if (existing.isEmpty()) {
            return openSession(phone);
        }

        ConversationSession session = existing.get();
        log.debug("Session found. phone={} state={}", phone, session.getState());

        return switch (session.getState()) {
            case WAITING_CUIT  -> handleCuit(session, message);
            case WAITING_FILES -> handleFiles(session, message);
            case PROCESSING    -> Optional.of("Su solicitud está siendo procesada, por favor espere.");
            case COMPLETED     -> {
                sessionPort.delete(phone);
                log.info("Session COMPLETED — restarting flow. phone={}", phone);
                yield openSession(phone);
            }
        };
    }

    private Optional<String> openSession(String phone) {
        ConversationSession session = ConversationSession.builder()
                .phoneNumber(phone)
                .state(SessionState.WAITING_CUIT)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        sessionPort.save(session);
        log.info("New session created. phone={}", phone);
        return Optional.of("Bienvenido. Por favor ingrese el CUIT de su empresa para continuar.");
    }

    private Optional<String> handleCuit(ConversationSession session, InboundMessage message) {
        String cuit = message.getBody() != null ? message.getBody().trim() : "";
        log.info("Validating CUIT. phone={} cuit='{}'", session.getPhoneNumber(), cuit);

        if (cuit.isBlank()) {
            return Optional.of("Por favor ingrese un CUIT válido. Ejemplo: 30-12345678-9");
        }

        Optional<CompanyInfo> companyOpt = companyInfoPort.findByCuit(cuit);
        if (companyOpt.isEmpty()) {
            log.warn("CUIT not found or invalid — closing session. phone={} cuit='{}'", session.getPhoneNumber(), cuit);
            sessionPort.delete(session.getPhoneNumber());
            return Optional.of("CUIT inválido o empresa no encontrada. La sesión ha sido cerrada. Envíe cualquier mensaje para iniciar de nuevo.");
        }

        CompanyInfo company = companyOpt.get();
        session.setState(SessionState.WAITING_FILES);
        session.setCuit(cuit);
        session.setCompanyInfo(company);
        session.setUpdatedAt(LocalDateTime.now());
        sessionPort.save(session);

        log.info("CUIT validated. phone={} company='{}'", session.getPhoneNumber(), company.getCompanyName());
        return Optional.of("Empresa: " + company.getCompanyName() + ". Por favor envíe los archivos a procesar como adjuntos.");
    }

    private Optional<String> handleFiles(ConversationSession session, InboundMessage message) {
        if (message.getNumMedia() == 0) {
            log.debug("No media in WAITING_FILES state. phone={}", session.getPhoneNumber());
            return Optional.of("Por favor envíe los archivos adjuntos a procesar.");
        }

        log.info("Receiving {} file(s). phone={}", message.getNumMedia(), session.getPhoneNumber());

        for (MediaAttachment attachment : message.getMediaAttachments()) {
            byte[] bytes = mediaDownloaderPort.download(attachment);
            Path stored = mediaStoragePort.store(attachment, message.getMessageSid(), bytes);

            ReceivedFile receivedFile = ReceivedFile.builder()
                    .filename(stored.getFileName().toString())
                    .storedPath(stored.toString())
                    .processingId(UUID.randomUUID())
                    .build();
            session.getReceivedFiles().add(receivedFile);
            log.info("File stored. filename={} processingId={}", receivedFile.getFilename(), receivedFile.getProcessingId());
        }

        session.setState(SessionState.PROCESSING);
        session.setUpdatedAt(LocalDateTime.now());
        sessionPort.save(session);

        fileProcessingAsyncService.processFiles(session.getPhoneNumber());

        return Optional.of("Archivos recibidos (" + message.getNumMedia() + "). Serán procesados en breve, le notificaremos al finalizar.");
    }
}
