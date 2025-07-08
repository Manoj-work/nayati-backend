package com.medhir.rest.sales;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
@Getter
@Setter
@Document(collection = "leads")
@Data // Generates getters, setters, toString, equals, hashCode, and required constructors
@NoArgsConstructor
@AllArgsConstructor
public class LeadModl {

    @Id
    private String id; // MongoDB's default String ID

    private String leadId; // Business ID with prefix

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

    private String propertyType; // Optional
    private String location;     // Optional

    @Pattern(regexp = "^$|^[0-9,]+$", message = "Budget must be in valid numeric format like 10,00,000")
    private String budget;       // Optional

    @NotBlank(message = "Source is required")
    private String source;

    @NotBlank(message = "Status is required")
    private String status;

//    private LocalDate followUpDate; // Optional
//   @FutureOrPresent(message = "Follow-up date must be today or in the future")
//    private LocalDate followUpDate;


    @Pattern(regexp = "^[0-9]{4}-[0-9]{2}-[0-9]{2}$", message = "Date must be in yyyy-MM-dd format")
    private String followUpDate;


    @Size(max = 500, message = "Notes can have up to 500 characters")
    private String notes; // Optional
}
