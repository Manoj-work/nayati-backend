package com.medhir.rest.model.accountantModule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "vendors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorModel {

    @Id
    @JsonIgnore
    private String id;

    @Indexed(unique = true)
    private String vendorId;

    @NotBlank(message = "Company ID is required")
    private String companyId;

    @NotBlank(message = "Vendor name is required")
    @Size(max = 100, message = "Vendor name must not exceed 100 characters")
    private String vendorName;

    @NotBlank(message = "Company type is required")
    @Pattern(regexp = "Company|Individual", message = "Company type must be either 'Company' or 'Individual'")
    private String companyType;

    @NotBlank(message = "GST number is required")
    @Pattern(
            regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[0-9A-Z]{1}[Z]{1}[0-9A-Z]{1}$",
            message = "Invalid GST format. Must be a 15-character alphanumeric GSTIN."
    )
    private String gstin;

    @NotBlank(message = "PAN is required")
    @Pattern(
            regexp = "[A-Z]{5}[0-9]{4}[A-Z]{1}",
            message = "Invalid PAN format"
    )
    private String pan;

    @NotBlank(message = "Address Line 1 is required")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Country is required")
    private String country;

    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid PIN code")
    private String pinCode;

    private String phone;

    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    private String mobile;

    @Email(message = "Invalid email format")
    private String email;

    private String website;

    private List<String> vendorTags;

    @Valid
    private BankDetails bankDetails;

    @Valid
    private List<ContactAddress> contactAddresses;

    // ---------- Nested Classes ----------

    @Data
    public static class ContactAddress {
        @NotBlank(message = "Contact name is required")
        private String name;

        @NotBlank(message = "Contact type is required")
        private String type;

        @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
        private String phone;

        @Email(message = "Invalid contact email format")
        private String email;
    }

    @Data
    public static class BankDetails {
        @NotBlank(message = "Bank name is required")
        private String bankName;

        @NotBlank(message = "Account number is required")
        private String accountNumber;

        @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC Code format")
        private String ifscCode;

        @NotBlank(message = "Branch name is required")
        private String branchName;

        @NotBlank(message = "Account holder name is required")
        private String accountHolderName;
    }
}
