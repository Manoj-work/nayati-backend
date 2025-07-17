package com.medhir.rest.dto.company;

import lombok.Data;

import java.util.List;

@Data
public class CompanyResponseDTO {
    private String companyId;
    private String name;
    private String email;
    private String phone;
    private String gst;
    private String regAdd;
    private String prefixForEmpID;
    private String colorCode;
    private List<CompanyHeadResponseDTO> companyHeads;



}
