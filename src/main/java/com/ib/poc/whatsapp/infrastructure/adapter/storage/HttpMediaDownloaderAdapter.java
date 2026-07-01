package com.ib.poc.whatsapp.infrastructure.adapter.storage;

import com.ib.poc.whatsapp.domain.model.DownloadedMedia;
import com.ib.poc.whatsapp.domain.model.MediaAttachment;
import com.ib.poc.whatsapp.application.port.out.MediaDownloaderPort;
import feign.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;

@Component
public class HttpMediaDownloaderAdapter implements MediaDownloaderPort {

    private static final Logger log = LoggerFactory.getLogger(HttpMediaDownloaderAdapter.class);

    private final TwilioMediaFeignClient twilioMediaFeignClient;
    private final String basicAuth;

    public HttpMediaDownloaderAdapter(
            TwilioMediaFeignClient twilioMediaFeignClient,
            @Value("${twilio.account-sid}") String accountSid,
            @Value("${twilio.auth-token}") String authToken) {
        this.twilioMediaFeignClient = twilioMediaFeignClient;
        this.basicAuth = "Basic " + Base64.getEncoder()
                .encodeToString((accountSid + ":" + authToken).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public DownloadedMedia download(MediaAttachment attachment) {
        log.info("Downloading media. index={} url={} contentType={}",
                attachment.getIndex(), attachment.getUrl(), attachment.getContentType());

        long start = System.currentTimeMillis();
        Response response = twilioMediaFeignClient.download(URI.create(attachment.getUrl()), basicAuth);

        if (response.status() < 200 || response.status() >= 300) {
            throw new RuntimeException("Media download failed. status=" + response.status()
                    + " index=" + attachment.getIndex());
        }

        try {
            byte[] bytes = response.body().asInputStream().readAllBytes();
            String filename = resolveOriginalFilename(attachment, response);
            log.info("Media downloaded. index={} size={}bytes duration={}ms filename={}",
                    attachment.getIndex(), bytes.length, System.currentTimeMillis() - start, filename);
            return DownloadedMedia.builder().content(bytes).originalFilename(filename).build();
        } catch (IOException e) {
            throw new RuntimeException("Error reading media response. index=" + attachment.getIndex(), e);
        }
    }

    private String resolveOriginalFilename(MediaAttachment attachment, Response response) {
        if (attachment.getFilename() != null && !attachment.getFilename().isBlank()) {
            log.debug("Using webhook filename. index={} filename={}", attachment.getIndex(), attachment.getFilename());
            return attachment.getFilename();
        }
        Collection<String> header = response.headers().get("content-disposition");
        if (header == null || header.isEmpty()) return null;
        String cd = header.iterator().next();
        int idx = cd.toLowerCase().indexOf("filename=");
        if (idx < 0) return null;
        String after = cd.substring(idx + 9).trim();
        if (after.startsWith("\"")) {
            int end = after.indexOf('"', 1);
            return end > 0 ? after.substring(1, end) : null;
        }
        int end = after.indexOf(';');
        String result = end > 0 ? after.substring(0, end).trim() : after.trim();
        return result.isBlank() ? null : result;
    }
}
