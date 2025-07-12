package com.medhir.rest.sales.dto.pipeline;

import com.medhir.rest.sales.model.FormType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormTypeDTO {
    private String value;
    private String modalName;
    private String description;
    private String displayName;

    public static FormTypeDTO fromFormType(FormType formType) {
        return new FormTypeDTO(
            formType.getValue(),
            formType.getModalName(),
            formType.getDescription(),
            formType.getValue().charAt(0) + formType.getValue().substring(1).toLowerCase()
        );
    }
} 