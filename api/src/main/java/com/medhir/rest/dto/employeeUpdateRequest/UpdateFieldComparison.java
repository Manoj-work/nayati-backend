package com.medhir.rest.dto.employeeUpdateRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFieldComparison {
    private String fieldName;
    private String oldValue;
    private String newValue;
} 