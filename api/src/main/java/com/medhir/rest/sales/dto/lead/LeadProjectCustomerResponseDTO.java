package com.medhir.rest.sales.dto.lead;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadProjectCustomerResponseDTO {

private String projectId;
private String projectName;
private String customerId;
private String customerName;

}
