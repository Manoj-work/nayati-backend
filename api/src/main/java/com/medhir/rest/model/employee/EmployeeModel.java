package com.medhir.rest.model.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.medhir.rest.dto.employeeUpdateRequest.EmployeeUpdateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Document(collection = "employees")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields from JSON response
public class EmployeeModel {

    @Id
    @JsonIgnore
    private String id;

    // @NotBlank(message = "Employee Id cannot be empty")
    @Indexed(unique = true)
    private String employeeId = "";

    @NotBlank(message = "Company Id cannot be empty")
    private String companyId = "";
  
    private String name = "";

    @NotBlank(message = "first name cannot be empty")
    private String firstName="";

    private String middleName;

    @NotBlank(message = "last name cannot be empty")
    private String lastName="";

    public String getName() {
        boolean hasFirst = firstName != null && !firstName.trim().isEmpty();
        boolean hasLast = lastName != null && !lastName.trim().isEmpty();

        if (!hasFirst && !hasLast) {
            return name;
        }

        StringBuilder fullName = new StringBuilder();
        if (hasFirst) fullName.append(firstName.trim());
        if (middleName != null && !middleName.trim().isEmpty()) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(middleName.trim());
        }
        if (hasLast) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(lastName.trim());
        }
        return fullName.toString();
    }

    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    @Indexed(unique = true)
    private String phone = "";

    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    private String alternatePhone = "";

    private Set<String> Roles = new HashSet<>();
    private List<String> moduleIds = new ArrayList<>();

    private String emailPersonal = "";

    @Email(message = "Invalid email format!")
    @Indexed(unique = true)
    private String emailOfficial = "";

    private String designation = "";
    private String fathersName = "";
    private boolean overtimeEligibile = false;
    private boolean pfEnrolled = false;
    private String uanNumber = "";
    private boolean esicEnrolled = false;
    private String esicNumber = "";
    private List<String> weeklyOffs = new ArrayList<>();

    private String employeeImgUrl = "";

    private LocalDate joiningDate = null;
    private String department = "";
    private String gender = "";
    private String reportingManager = "";
    private String permanentAddress = "";
    private String currentAddress = "";

    // Leave related fields
    private String leavePolicyId = ""; // Stores the ID of the leave policy from department

    // ID Proofs Section
    @Valid
    private IdProofs idProofs = new IdProofs();

    // Bank Details Section
    @Valid
    private BankDetails bankDetails = new BankDetails();

    // Salary Details Section
    @Valid
    private SalaryDetails salaryDetails = new SalaryDetails();

    private String updateStatus = ""; // Approved, Pending, Rejected

    private List<String> assignTo = new ArrayList<>(); // Field to store who the employee is assigned to

    @Valid
    private EmployeeUpdateRequest pendingUpdateRequest = null; // Reference to EmployeeUpdateRequest which stores the pending
                                                        // updates

    @Getter
    @Setter
    public static class IdProofs {
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
    public static class BankDetails {
        @Pattern(regexp = "\\d{9,18}", message = "Account number must be between 9 to 18 digits")
        private String accountNumber = "";

        private String accountHolderName = "";

        @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC Code format")
        private String ifscCode = "";

        private String bankName = "";
        private String branchName = "";
        private String upiId = "";
        private String upiPhoneNumber = "";

        private String passbookImgUrl = "";
    }

    @Getter
    @Setter
    public static class SalaryDetails {
        private Double annualCtc = 0.0;
        private Double monthlyCtc = 0.0;
        private Double basicSalary = 0.0;
        private Double hra = 0.0;
        private Double allowances = 0.0;
        private Double employerPfContribution = 0.0;
        private Double employeePfContribution = 0.0;
    }
    private List<String> roleIds;

}
