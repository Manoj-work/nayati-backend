package com.medhir.rest.sales.dto.pipeline;

import com.medhir.rest.sales.model.FormType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePipelineStageRequest {
    private String name;
    private String description;
    private String color;
    private Boolean isActive;
    private Boolean isForm;
    private FormType formType;
    
    // Additional field to handle isFormRequired (for backward compatibility)
    private Boolean isFormRequired;

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

    // Custom setter for isFormRequired to sync with isForm
    public void setIsFormRequired(Boolean isFormRequired) {
        this.isFormRequired = isFormRequired;
        if (isFormRequired != null) {
            this.isForm = isFormRequired;
        }
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getColor() { return color; }
    public Boolean getIsActive() { return isActive; }
    public Boolean getIsForm() { return isForm; }
    public FormType getFormType() { return formType; }
    public Boolean getIsFormRequired() { return isFormRequired; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setColor(String color) { this.color = color; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public void setIsForm(Boolean isForm) { this.isForm = isForm; }
} 