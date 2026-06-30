package com.ib.poc.whatsapp.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TwilioWebhookParamsDto {
    private String from;
    private String to;
    private String body;
    private String messageSid;
    private String profileName;
    private int numMedia;
    // Populated from dynamic MediaUrl0..N and MediaContentType0..N params
    private List<String> mediaUrls;
    private List<String> mediaContentTypes;
}
