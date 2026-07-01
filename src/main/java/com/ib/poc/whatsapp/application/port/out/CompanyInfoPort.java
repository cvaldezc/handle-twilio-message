package com.ib.poc.whatsapp.application.port.out;

import com.ib.poc.whatsapp.domain.model.CompanyInfo;

import java.util.Optional;

public interface CompanyInfoPort {
    Optional<CompanyInfo> findByCuit(String cuit);
}
