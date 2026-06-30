package com.ib.poc.whatsapp.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "media")
public class MediaProperties {
    private String storagePath = "./media-downloads";
}
