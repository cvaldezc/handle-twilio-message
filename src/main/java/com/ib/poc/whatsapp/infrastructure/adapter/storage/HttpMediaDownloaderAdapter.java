package com.ib.poc.whatsapp.infrastructure.adapter.storage;

import com.ib.poc.whatsapp.domain.model.MediaAttachment;
import com.ib.poc.whatsapp.application.port.out.MediaDownloaderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class HttpMediaDownloaderAdapter implements MediaDownloaderPort {

    private static final Logger log = LoggerFactory.getLogger(HttpMediaDownloaderAdapter.class);

    private final String basicAuth;
    private final HttpClient httpClient;

    public HttpMediaDownloaderAdapter(
            @Value("${twilio.account-sid}") String accountSid,
            @Value("${twilio.auth-token}") String authToken) {
        this.basicAuth = "Basic " + Base64.getEncoder()
                .encodeToString((accountSid + ":" + authToken).getBytes(StandardCharsets.UTF_8));
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    @Override
    public byte[] download(MediaAttachment attachment) {
        log.info("Downloading media. index={} url={} contentType={}",
                attachment.getIndex(), attachment.getUrl(), attachment.getContentType());

        long start = System.currentTimeMillis();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(attachment.getUrl()))
                    .header("Authorization", basicAuth)
                    .GET()
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Media download failed. status=" + response.statusCode()
                        + " index=" + attachment.getIndex());
            }

            byte[] bytes = response.body();
            log.info("Media downloaded. index={} size={}bytes duration={}ms",
                    attachment.getIndex(), bytes.length, System.currentTimeMillis() - start);
            return bytes;

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Media download error for index " + attachment.getIndex(), e);
        }
    }
}
