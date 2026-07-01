package com.ib.poc.whatsapp.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationSession {
    String phoneNumber;
    SessionState state;
    String cuit;
    CompanyInfo companyInfo;
    @Builder.Default List<ReceivedFile> receivedFiles = new ArrayList<>();
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
