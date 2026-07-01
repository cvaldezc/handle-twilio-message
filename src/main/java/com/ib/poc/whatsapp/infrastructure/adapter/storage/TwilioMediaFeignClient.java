package com.ib.poc.whatsapp.infrastructure.adapter.storage;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.net.URI;

@FeignClient(name = "twilio-media", url = "https://api.twilio.com")
public interface TwilioMediaFeignClient {

    @GetMapping
    Response download(URI mediaUri, @RequestHeader("Authorization") String auth);
}
