package com.medhir.rest.dto.company;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateCompanyWithHeadRequest {

    @Valid
    @NotNull
    private CompanyDetailsDTO company;

    @Valid
    @NotNull
    private List<CompanyHeadDetailsDTO> companyHeads;

}
