package com.ib.poc.whatsapp.infrastructure.adapter.company;

import com.ib.poc.whatsapp.application.port.out.CompanyInfoPort;
import com.ib.poc.whatsapp.domain.model.CompanyInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@Primary
public class MockCompanyInfoAdapter implements CompanyInfoPort {

    private static final Logger log = LoggerFactory.getLogger(MockCompanyInfoAdapter.class);

    private static final Map<String, CompanyInfo> MOCK_COMPANIES = Map.of(
            "30123456789", CompanyInfo.builder().cuit("30-12345678-9").companyName("Empresa Demo SA").enabled(true).build(),
            "20987654321", CompanyInfo.builder().cuit("20-98765432-1").companyName("Comercio Ejemplo SRL").enabled(true).build(),
            "33111111111", CompanyInfo.builder().cuit("33-11111111-1").companyName("Test Corp SA").enabled(true).build()
    );

    @Override
    public Optional<CompanyInfo> findByCuit(String cuit) {
        log.debug("[MOCK] findByCuit. cuit={}", cuit);
        Optional<CompanyInfo> result = Optional.ofNullable(MOCK_COMPANIES.get(cuit));
        log.info("[MOCK] CompanyManager response. cuit={} found={}", cuit, result.isPresent());
        return result;
    }
}
