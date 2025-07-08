package com.medhir.rest.sales.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadDto {

    private String id;  // Optional for create; Required for update

    private String leadId;  // Business ID, generated on server-side

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phone;

    @NotBlank(message = "Project type is required")
    private String projectType;

    private String propertyType;
    private String location;

    @Pattern(regexp = "^$|^[0-9,]+$", message = "Budget must be in valid numeric format like 10,00,000")
    private String budget;

    @NotBlank(message = "Source is required")
    private String source;

    @NotBlank(message = "Status is required")
    private String status;

//    @FutureOrPresent(message = "Follow-up date must be today or in the future")
//    private LocalDate followUpDate;
    @Pattern(regexp = "^[0-9]{4}-[0-9]{2}-[0-9]{2}$", message = "Date must be in yyyy-MM-dd format")
    private String followUpDate;


    @Size(max = 500, message = "Notes can have up to 500 characters")
    private String notes;
}
