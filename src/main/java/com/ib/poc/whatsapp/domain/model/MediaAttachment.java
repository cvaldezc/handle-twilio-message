package com.ib.poc.whatsapp.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MediaAttachment {
    int index;
    String url;
    String contentType;
}
