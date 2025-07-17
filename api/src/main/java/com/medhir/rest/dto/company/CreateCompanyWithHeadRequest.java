package com.medhir.rest.dto.company;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCompanyWithHeadRequest {

    @Valid
    @NotNull
    private CompanyDetailsDTO company;

    @Valid
    @NotNull
    private CompanyHeadDetailsDTO companyHead;

}
