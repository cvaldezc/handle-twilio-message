package com.ib.poc.whatsapp.infrastructure.adapter.company;

import com.ib.poc.whatsapp.application.port.out.CompanyInfoPort;
import com.ib.poc.whatsapp.domain.model.CompanyInfo;
import com.ib.poc.whatsapp.infrastructure.adapter.company.dto.CompanyInfoResponse;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompanyManagerFeignAdapter implements CompanyInfoPort {

    private static final Logger log = LoggerFactory.getLogger(CompanyManagerFeignAdapter.class);

    private final CompanyManagerFeignClient feignClient;

    public CompanyManagerFeignAdapter(CompanyManagerFeignClient feignClient) {
        this.feignClient = feignClient;
    }

    @Override
    public Optional<CompanyInfo> findByCuit(String cuit) {
        log.debug("Calling CompanyManager. cuit={}", cuit);
        try {
            CompanyInfoResponse response = feignClient.getCompanyByCuit(cuit);
            if (response == null || !response.isEnabled()) {
                log.info("Company not enabled or null response. cuit={}", cuit);
                return Optional.empty();
            }
            return Optional.of(CompanyInfo.builder()
                    .cuit(response.getCuit())
                    .companyName(response.getCompanyName())
                    .enabled(response.isEnabled())
                    .build());
        } catch (FeignException.NotFound e) {
            log.info("Company not found. cuit={}", cuit);
            return Optional.empty();
        } catch (FeignException e) {
            log.error("CompanyManager call failed. cuit={} status={}", cuit, e.status(), e);
            return Optional.empty();
        }
    }
}
