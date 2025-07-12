package com.medhir.rest.service.settings;

import com.medhir.rest.exception.BadRequestException;
import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.model.settings.LeavePolicyModel;
import com.medhir.rest.repository.settings.LeavePolicyRepository;
import com.medhir.rest.service.CompanyService;
import com.medhir.rest.utils.GeneratedId;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class LeavePolicyService {

    @Autowired
    private LeavePolicyRepository leavePolicyRepository;

    @Autowired
    private LeaveTypeService leaveTypeService;


    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Autowired
    private CompanyService companyService;

    public LeavePolicyModel createLeavePolicy(LeavePolicyModel leavePolicy) {
        // Check if company exists
        companyService.getCompanyById(leavePolicy.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + leavePolicy.getCompanyId()));

        if (leavePolicyRepository.existsByName(leavePolicy.getName())) {
            throw new DuplicateResourceException("Leave policy with name " + leavePolicy.getName() + " already exists");
        }

        validateLeaveAllocations(leavePolicy.getLeaveAllocations());



        String newLeavePolicyId = "LP" + snowflakeIdGenerator.nextId();

        leavePolicy.setLeavePolicyId(newLeavePolicyId);

        leavePolicy.setCreatedAt(LocalDateTime.now().toString());
        leavePolicy.setUpdatedAt(LocalDateTime.now().toString());
        return leavePolicyRepository.save(leavePolicy);
    }

    public List<LeavePolicyModel> getAllLeavePolicies() {
        return leavePolicyRepository.findAll();
    }

    public List<LeavePolicyModel> getLeavePoliciesByCompanyId(String companyId) {
        return leavePolicyRepository.findByCompanyId(companyId);
    }

    public LeavePolicyModel getLeavePolicyById(String id) {
        // First try to find by leavePolicyId
        LeavePolicyModel leavePolicy = leavePolicyRepository.findByLeavePolicyId(id)
                .orElse(null);

        // If not found by leavePolicyId, try by MongoDB id
        if (leavePolicy == null) {
            leavePolicy = leavePolicyRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Leave policy not found with id: " + id));
        }

        return leavePolicy;
    }

    public LeavePolicyModel updateLeavePolicy(String id, LeavePolicyModel leavePolicy) {
        LeavePolicyModel existingPolicy = getLeavePolicyById(id);

        // Check if company exists if companyId is being updated
        if (leavePolicy.getCompanyId() != null && !leavePolicy.getCompanyId().equals(existingPolicy.getCompanyId())) {
            companyService.getCompanyById(leavePolicy.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + leavePolicy.getCompanyId()));
        }

        if (!existingPolicy.getName().equals(leavePolicy.getName()) &&
                leavePolicyRepository.existsByName(leavePolicy.getName())) {
            throw new DuplicateResourceException("Leave policy with name " + leavePolicy.getName() + " already exists");
        }

        validateLeaveAllocations(leavePolicy.getLeaveAllocations());

        existingPolicy.setName(leavePolicy.getName());
        existingPolicy.setLeaveAllocations(leavePolicy.getLeaveAllocations());
        existingPolicy.setUpdatedAt(LocalDateTime.now().toString());

        // Update companyId if provided
        if (leavePolicy.getCompanyId() != null) {
            existingPolicy.setCompanyId(leavePolicy.getCompanyId());
        }

        return leavePolicyRepository.save(existingPolicy);
    }

    public void deleteLeavePolicy(String id) {
        LeavePolicyModel leavePolicy = getLeavePolicyById(id);
        leavePolicyRepository.deleteById(leavePolicy.getId());
    }

    private void validateLeaveAllocations(@NotEmpty @Valid List<LeavePolicyModel.LeaveAllocation> allocations) {
        if (allocations == null || allocations.isEmpty()) {
            throw new BadRequestException("Leave allocations cannot be empty");
        }

        for (LeavePolicyModel.LeaveAllocation allocation : allocations) {
            // Verify leave type exists
            leaveTypeService.getLeaveTypeById(allocation.getLeaveTypeId());

            // Validate days per year
            if (allocation.getDaysPerYear() == null || allocation.getDaysPerYear() <= 0) {
                throw new BadRequestException("Days per year must be greater than 0");
            }

            // Validate consecutive allowed flag - if null, it will use the default value of false
            if (allocation.getConsecutiveAllowed() == null) {
                allocation.setConsecutiveAllowed(false);
            }

            // Validate restrictions if present
            if (allocation.getRestrictions() != null && !allocation.getRestrictions().isEmpty()) {
                for (LeavePolicyModel.LeaveRestriction restriction : allocation.getRestrictions()) {
                    if (restriction.getRestrictedDays() == null || restriction.getRestrictedDays().isEmpty()) {
                        throw new BadRequestException("Restricted days cannot be empty");
                    }
                    // Set default value for allowedValue if not provided
                    if (restriction.getAllowedValue() == null) {
                        restriction.setAllowedValue(1);
                    } else if (restriction.getAllowedValue() <= 0) {
                        throw new BadRequestException("Allowed value must be greater than 0");
                    }
                }
            }
        }
    }

    public List<LeavePolicyModel> getLeavePoliciesByIds(Set<String> ids) {
        return leavePolicyRepository.findByLeavePolicyIdIn(ids);
    }
} 