package com.ib.poc.whatsapp.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for POST /api/whatsapp/send
 * Example: { "to": "+521234567890", "message": "Hello!" }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequestDto {
    private String to;
    private String message;
}
