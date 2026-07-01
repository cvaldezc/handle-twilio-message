package com.ib.poc.whatsapp.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DownloadedMedia {
    byte[] content;
    String originalFilename; // null if Content-Disposition absent in download response
}
