package com.ib.poc.whatsapp.infrastructure.adapter.company;

import com.ib.poc.whatsapp.infrastructure.adapter.company.dto.CompanyInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "company-manager", url = "${company-manager.url}")
public interface CompanyManagerFeignClient {

    @GetMapping("/api/companies/{cuit}")
    CompanyInfoResponse getCompanyByCuit(@PathVariable("cuit") String cuit);
}
