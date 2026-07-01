package com.ib.poc.whatsapp.application.port.out;

import com.ib.poc.whatsapp.domain.model.ConversationSession;

import java.util.Optional;

public interface ConversationSessionPort {
    Optional<ConversationSession> findByPhone(String phoneNumber);
    void save(ConversationSession session);
    void delete(String phoneNumber);
}
