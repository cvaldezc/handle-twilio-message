package com.ib.poc.whatsapp.infrastructure.adapter.storage;

import com.ib.poc.whatsapp.domain.model.MediaAttachment;
import com.ib.poc.whatsapp.application.port.out.MediaStoragePort;
import com.ib.poc.whatsapp.infrastructure.config.MediaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class LocalDiskMediaStorageAdapter implements MediaStoragePort {

    private static final Logger log = LoggerFactory.getLogger(LocalDiskMediaStorageAdapter.class);

    private final MediaProperties mediaProperties;

    public LocalDiskMediaStorageAdapter(MediaProperties mediaProperties) {
        this.mediaProperties = mediaProperties;
    }

    @Override
    public Path store(MediaAttachment attachment, String messageId, byte[] content) {
        String dateFolder = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        Path dir = Path.of(mediaProperties.getStoragePath()).resolve(dateFolder);
        try {
            Files.createDirectories(dir);
            String filename = resolveFilename(attachment);
            Path target = dir.resolve(filename);
            Files.write(target, content);
            log.info("Media stored. index={} path={} size={}bytes",
                    attachment.getIndex(), target, content.length);
            return target;
        } catch (IOException e) {
            log.error("Failed to store media. index={} messageId={} error={}",
                    attachment.getIndex(), messageId, e.getMessage(), e);
            throw new RuntimeException("Media storage failed for index " + attachment.getIndex(), e);
        }
    }

    private String resolveFilename(MediaAttachment attachment) {
        String ext = extensionFromContentType(attachment.getContentType());
        try {
            String path = URI.create(attachment.getUrl()).getPath();
            String lastSegment = path.substring(path.lastIndexOf('/') + 1);
            if (!lastSegment.isBlank()) {
                return attachment.getIndex() + "_" + lastSegment + ext;
            }
        } catch (Exception ignored) {
            // fallback below
        }
        return "media_" + attachment.getIndex() + ext;
    }

    private String extensionFromContentType(String contentType) {
        if (contentType == null) return ".bin";
        return switch (contentType.toLowerCase()) {
            case "image/jpeg" -> ".jpg";
            case "image/png"  -> ".png";
            case "image/gif"  -> ".gif";
            case "image/webp" -> ".webp";
            case "audio/ogg"  -> ".ogg";
            case "audio/mpeg" -> ".mp3";
            case "video/mp4"  -> ".mp4";
            case "application/pdf" -> ".pdf";
            default -> ".bin";
        };
    }
}
