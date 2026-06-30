package com.ib.poc.whatsapp.domain.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder(toBuilder = true)
public class InboundMessage {
    String messageSid;
    String from;       // phone number without whatsapp: prefix
    String to;         // phone number without whatsapp: prefix
    String body;
    String profileName;
    int numMedia;
    @Builder.Default List<MediaAttachment> mediaAttachments = List.of();
}
