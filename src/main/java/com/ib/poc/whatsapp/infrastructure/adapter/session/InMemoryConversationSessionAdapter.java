package com.ib.poc.whatsapp.infrastructure.adapter.session;

import com.ib.poc.whatsapp.application.port.out.ConversationSessionPort;
import com.ib.poc.whatsapp.domain.model.ConversationSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryConversationSessionAdapter implements ConversationSessionPort {

    private static final Logger log = LoggerFactory.getLogger(InMemoryConversationSessionAdapter.class);

    private final ConcurrentHashMap<String, ConversationSession> store = new ConcurrentHashMap<>();

    @Override
    public Optional<ConversationSession> findByPhone(String phoneNumber) {
        return Optional.ofNullable(store.get(phoneNumber));
    }

    @Override
    public void save(ConversationSession session) {
        store.put(session.getPhoneNumber(), session);
        log.debug("Session saved. phone={} state={}", session.getPhoneNumber(), session.getState());
    }

    @Override
    public void delete(String phoneNumber) {
        store.remove(phoneNumber);
        log.debug("Session deleted. phone={}", phoneNumber);
    }
}
