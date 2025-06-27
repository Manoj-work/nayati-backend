package com.medhir.rest.accounting;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BillLine {
    private String description;
    private Double amount;
    // Add other fields as needed
}
