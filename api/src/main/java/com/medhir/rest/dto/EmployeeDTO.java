package com.medhir.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class EmployeeDTO {
    private String employeeId;

    @NotBlank(message = "Company Id cannot be empty")
    private String companyId;

    private String name;

    @NotBlank
    private String firstName;

    private String middleName;

    @NotBlank
    private String lastName;

    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    @NotBlank(message = "Phone number cannot be empty")
    private String phone;

    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    private String alternatePhone;

    private Set<String> roles;
    private List<String> moduleIds;

    @NotNull(message = "Email is required!")
    @Email(message = "Invalid email format!")
    private String emailPersonal;

    @Email(message = "Invalid email format!")
    private String emailOfficial;

    private String designation;
    private String fathersName;
    private boolean overtimeEligibile;
    private boolean pfEnrolled;
    private String uanNumber;
    private boolean esicEnrolled;
    private String esicNumber;
    private List<String> weeklyOffs;
    private String employeeImgUrl = "";
    private LocalDate joiningDate;
    private String department;
    private String gender;
    private String reportingManager;
    private String permanentAddress;
    private String currentAddress;
    private String leavePolicyId;
    @Valid
    private IdProofsDTO idProofs;
    @Valid
    private BankDetailsDTO bankDetails;
    @Valid
    private SalaryDetailsDTO salaryDetails;
    private String updateStatus;
    private List<String> assignTo;

    @Getter
    @Setter
    public static class IdProofsDTO {
        @Pattern(regexp = "^$|\\d{12}", message = "Aadhar number must be exactly 12 digits or empty")
        private String aadharNo = "";
        private String aadharImgUrl = "";
        @Pattern(regexp = "^$|[A-Z]{5}[0-9]{4}[A-Z]{1}", message = "Invalid PAN number format or empty")
        private String panNo = "";
        private String pancardImgUrl = "";
        @Pattern(regexp = "^$|^[A-Z]{1}[0-9]{7}$", message = "Invalid Passport number format or empty")
        private String passport = "";
        private String passportImgUrl = "";
        @Pattern(regexp = "^$|^[A-Za-z0-9]{8,16}$", message = "Invalid Driving License format or empty")
        private String drivingLicense = "";
        private String drivingLicenseImgUrl = "";
        @Pattern(regexp = "^$|^[A-Z]{3}[0-9]{7}$", message = "Invalid Voter ID format or empty")
        private String voterId = "";
        private String voterIdImgUrl = "";
    }

    @Getter
    @Setter
    public static class BankDetailsDTO {
        @Pattern(regexp = "\\d{9,18}", message = "Account number must be between 9 to 18 digits")
        @Size(min = 0)
        private String accountNumber = "";
        private String accountHolderName = "";
        @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC Code format")
        @Size(min = 0)
        private String ifscCode = "";
        private String bankName = "";
        private String branchName = "";
        private String upiId = "";
        private String upiPhoneNumber = "";
        private String passbookImgUrl;
    }

    @Getter
    @Setter
    public static class SalaryDetailsDTO {
        private Double annualCtc = 0.0;
        private Double monthlyCtc = 0.0;
        private Double basicSalary = 0.0;
        private Double hra = 0.0;
        private Double allowances = 0.0;
        private Double employerPfContribution = 0.0;
        private Double employeePfContribution = 0.0;
    }
} 