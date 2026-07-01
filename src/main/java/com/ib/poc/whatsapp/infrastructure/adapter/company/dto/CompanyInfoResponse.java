package com.ib.poc.whatsapp.infrastructure.adapter.company.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyInfoResponse {
    private String cuit;
    private String companyName;
    private boolean enabled;
}
