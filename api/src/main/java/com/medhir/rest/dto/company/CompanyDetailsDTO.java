package com.medhir.rest.dto.company;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyDetailsDTO {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "\\d{10}")
    private String phone;

    @NotBlank
    private String gst;

    @NotBlank
    private String regAdd;

    private String prefixForEmpID;

}
