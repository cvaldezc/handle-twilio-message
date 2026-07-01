package com.ib.poc.whatsapp.domain.model;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class ReceivedFile {
    String filename;
    String storedPath;
    UUID processingId;
}
