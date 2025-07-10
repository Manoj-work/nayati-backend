package com.medhir.rest.dto.accountantModule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VendorCreditUpdateRequest {
    private List<VendorCreditDTO> vendorCredits;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VendorCreditDTO {
        private String creditAmount;
        private String creditDate;
        private String creditDescription;
    }
} 