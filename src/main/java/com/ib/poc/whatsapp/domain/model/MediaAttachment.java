package com.ib.poc.whatsapp.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MediaAttachment {
    int index;
    String url;
    String contentType;
    String filename; // from MediaFilename{N} webhook param, null if not provided by Twilio
}
