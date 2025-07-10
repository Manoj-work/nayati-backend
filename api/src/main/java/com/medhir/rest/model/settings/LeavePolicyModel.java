package com.medhir.rest.model.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "leave_policies")
public class LeavePolicyModel {

    @Id
    @JsonIgnore
    private String id;

    private String leavePolicyId;

    @NotBlank(message = "Policy name is required")
    private String name;

    @NotBlank(message = "Company ID is required")
    private String companyId;

    @NotEmpty(message = "Leave allocations are required")
    @Valid
    private List<LeaveAllocation> leaveAllocations;

    private String createdAt;
    private String updatedAt;




    @Data
    public static class LeaveAllocation {
        @NotBlank(message = "Leave type ID is required")
        private String leaveTypeId;

        @NotNull(message = "Days per year is required")
        private Integer daysPerYear;

        @NotNull(message = "Consecutive allowed flag is required")
        private Boolean consecutiveAllowed = false;

        private List<LeaveRestriction> restrictions;
    }

    @Data
    public static class LeaveRestriction {
        private List<String> restrictedDays;
        private Integer allowedValue;
    }
} 