package com.ib.poc.whatsapp.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CompanyInfo {
    String cuit;
    String companyName;
    boolean enabled;
}
