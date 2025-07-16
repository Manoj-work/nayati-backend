package com.medhir.rest.dto.company;

import lombok.Data;

@Data
public class CompanyHeadResponseDTO {
    private String employeeId;
    private String name;
    private String email;
    private String phone;
}
