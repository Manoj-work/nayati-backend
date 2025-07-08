package com.medhir.rest.sales.dto.pipeline;

import com.medhir.rest.sales.model.FormType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
public class CreatePipelineStageRequest {
    @NotBlank(message = "Stage name is required")
    private String name;
    
    private String description;
    private String color;
    private boolean isForm;
    private FormType formType;
    
    // Additional field to handle isFormRequired (for backward compatibility)
    private Boolean isFormRequired;

    // Custom setter for isForm to add debug logging
    public void setIsForm(boolean isForm) {
        this.isForm = isForm;
    }

    // Custom setter for isFormRequired to sync with isForm
    public void setIsFormRequired(Boolean isFormRequired) {
        this.isFormRequired = isFormRequired;
        if (isFormRequired != null) {
            this.isForm = isFormRequired;
        }
    }

    // Custom setter to handle string to enum conversion
    public void setFormType(String formTypeString) {
        if (formTypeString != null && !formTypeString.trim().isEmpty()) {
            try {
                this.formType = FormType.fromValue(formTypeString);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid form type: " + formTypeString + ". Valid types are: " + 
                    java.util.Arrays.stream(FormType.values())
                        .map(FormType::getValue)
                        .collect(java.util.stream.Collectors.joining(", ")));
            }
        } else {
            this.formType = null;
        }
    }

    // Keep the original setter for enum values
    public void setFormType(FormType formType) {
        this.formType = formType;
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getColor() { return color; }
    public boolean isForm() { return isForm; }
    public FormType getFormType() { return formType; }
    public Boolean getIsFormRequired() { return isFormRequired; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setColor(String color) { this.color = color; }
} 