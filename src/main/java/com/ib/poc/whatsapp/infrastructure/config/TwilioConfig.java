package com.ib.poc.whatsapp.infrastructure.config;

import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

@Configuration
public class TwilioConfig {

    private static final Logger log = LoggerFactory.getLogger(TwilioConfig.class);

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @PostConstruct
    public void init() {
        log.info("Initializing Twilio client...");
        Assert.hasText(accountSid, "twilio.account-sid must not be blank — set TWILIO_ACCOUNT_SID env var or use application-local.properties");
        Assert.hasText(authToken,  "twilio.auth-token must not be blank  — set TWILIO_AUTH_TOKEN env var or use application-local.properties");
        log.debug("Account SID={} | Auth token length={}", accountSid, authToken.length());
        Twilio.init(accountSid, authToken);
        log.info("Twilio client ready. Account SID: {}", accountSid);
    }
}
