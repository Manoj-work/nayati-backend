package com.medhir.rest.model.accountantModule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Document(collection = "vendors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
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

    private List<String> vendorTags;

    // @NotBlank(message = "Company type is required")
    // @Pattern(regexp = "Company|Individual", message = "Company type must be either 'Company' or 'Individual'")
    // private String companyType;

    // @NotBlank(message = "Vendor Category is required")
    // private String vendorCategory;

    // @NotBlank(message = "GST number is required")
    private String gstin;

    @NotBlank(message = "PAN is required")
    @Pattern(
            regexp = "[A-Z]{5}[0-9]{4}[A-Z]{1}",
            message = "Invalid PAN format"
    )
    private String pan;

    @NotBlank(message = "Tax Treatment is required")
    private String taxTreatment;

    // @NotNull(message = "TDS is required")
    // private Boolean tds;

    // @NotNull(message = "TDS Percentage is required")
    @Min(value = 0, message = "TDS Percentage must be greater than 0")
    @Max(value = 10, message = "TDS Percentage must be less than 10")
    private Double tdsPercentage;

    @NotBlank(message = "Contact name is required")
    private String contactName;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    private String mobile;

    private String phone;

    @NotBlank(message = "Address Line 1 is required")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid PIN code")
    private String pinCode;


    @Valid
    private BankDetails bankDetails;

    @Data
    public static class BankDetails {
        @NotBlank(message = "Account holder name is required")
        private String accountHolderName;

        @NotBlank(message = "Branch name is required")
        private String branchName;

        @NotBlank(message = "Bank name is required")
        private String bankName;

        @NotBlank(message = "Bank account type is required")
        private String accountType;

        @NotBlank(message = "Account number is required")
        @Pattern(regexp = "^[0-9]{9,18}$", message = "Invalid account number format")
        private String accountNumber;

        @NotBlank(message = "IFSC Code is required")
        @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC Code format")
        private String ifscCode;

        @NotBlank(message = "UPI ID is required")
        @Email(message = "Invalid UPI ID format")
        private String upiId;
    }

    @Valid
    @NotEmpty(message = "At least one vendor credit is required")
    private List<VendorCredit> vendorCredits;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VendorCredit {
        private String creditAmount;
        private String creditDate;
        private String creditDescription;
    }

    @Valid
    private List<AdjustedPayments> adjustedPayments;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AdjustedPayments {
        @NotBlank(message = "Adjusted amount is required")
        private String adjustedAmount;
        
        @NotBlank(message = "Adjusted date is required")
        private String adjustedDate;
        
        @NotBlank(message = "Adjusted payment ID is required")
        private String adjustedPaymentId;
    }

    private BigDecimal totalCredit;
}

//    @Valid
//    private List<ContactAddress> contactAddresses;
//
//    // ---------- Nested Classes ----------
//
//    @Data
//    public static class ContactAddress {
//
//
//        @NotBlank(message = "Contact type is required")
//        private String type;
//
//        @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
//        private String phone;
//
//        @Email(message = "Invalid contact email format")
//        private String email;
//    }


