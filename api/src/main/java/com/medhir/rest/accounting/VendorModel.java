package com.medhir.rest.accounting;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document(collection = "vendors")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VendorModel {

    @Id

    private String id;

    @NotBlank(message = "Vendor name cannot be empty")
    @Indexed(unique = true)
    private String vendorName;

    @NotBlank(message = "Company or Individual field cannot be empty")
    private String companyOrIndividual;


    @Pattern(regexp = "^$|^[0-9A-Z]{15}$", message = "GSTIN must be 15 characters or empty")
    @NotBlank(message = "Address Line 1 cannot be empty")
    private String gstin;

    @Pattern(regexp = "^$|[A-Z]{5}[0-9]{4}[A-Z]{1}", message = "Invalid PAN number format or empty")
    private String pan;

    @NotBlank(message = "Address Line 1 cannot be empty")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "City cannot be empty")
    private String city;

    @NotBlank(message = "State cannot be empty")
    private String state;

    @Pattern(regexp = "^\\d{6}$", message = "Pin code must be exactly 6 digits")
    private String pinCode;

    @NotBlank(message = "Country cannot be empty")
    private String country;

    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    private String phone;

    @Pattern(regexp = "\\d{10}", message = "Mobile number must be exactly 10 digits")
    private String mobile;

    @Email(message = "Invalid email format")
    private String email;

    private String website;

    private List<String> vendorTags = new ArrayList<>();

    @Valid
    private List<Contact> contacts = new ArrayList<>();

    @Valid
    private List<BankingDetail> bankingDetails = new ArrayList<>();

    // --- Embedded Classes ---

    @Getter
    @Setter
    public static class Contact {
        @NotBlank(message = "Contact name cannot be empty")
        private String name;

        @NotBlank(message = "Contact type cannot be empty")
        private String type; // e.g., Billing, Shipping, etc.

        @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
        private String phone;

        @Email(message = "Invalid email format")
        private String email;
    }

    @Getter
    @Setter
    public static class BankingDetail {
        @Pattern(regexp = "\\d{9,18}", message = "Account number must be between 9 and 18 digits")
        private String accountNumber;

        private String accountHolderName;

        @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC code format")
        private String ifscCode;

        private String bankName;

        private String branchName;
    }
}
