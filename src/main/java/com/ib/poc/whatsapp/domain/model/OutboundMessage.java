package com.ib.poc.whatsapp.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OutboundMessage {
    String to;
    String body;
}
