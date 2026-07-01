package com.ib.poc.whatsapp.infrastructure.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.ib.poc.whatsapp.infrastructure.adapter")
public class FeignConfig {
}
