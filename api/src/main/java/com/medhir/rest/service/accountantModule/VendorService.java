package com.medhir.rest.service.accountantModule;

import com.medhir.rest.model.accountantModule.VendorModel;
import com.medhir.rest.repository.accountantModule.VendorRepository;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import com.medhir.rest.service.CompanyService;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.dto.accountantModule.VendorCreditUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class VendorService {

    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;
    @Autowired
    private CompanyService companyService;

    private static final Pattern GSTIN_PATTERN = Pattern.compile("^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[0-9A-Z]{1}[Z]{1}[0-9A-Z]{1}$");

    private void validateGstin(String gstin) {
        if (gstin != null && !gstin.trim().isEmpty()) {
            if (!GSTIN_PATTERN.matcher(gstin).matches()) {
                throw new IllegalArgumentException("Invalid GST format. Must be a 15-character alphanumeric GSTIN.");
            }
        }
    }

    public VendorModel createVendor(VendorModel vendor) {
        // Check if company exists
        companyService.getCompanyById(vendor.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + vendor.getCompanyId()));

        // Validate GSTIN if provided
        validateGstin(vendor.getGstin());

        vendor.setVendorId("VID" + snowflakeIdGenerator.nextId());
        return vendorRepository.save(vendor);
    }

    public VendorModel updateVendor(String vendorId, VendorModel updatedVendor) {
        VendorModel existingVendor = vendorRepository.findByVendorId(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor with ID " + vendorId + " not found"));
        // Check if company exists (if companyId is being updated)
        if (updatedVendor.getCompanyId() != null && !updatedVendor.getCompanyId().equals(existingVendor.getCompanyId())) {
            companyService.getCompanyById(updatedVendor.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + updatedVendor.getCompanyId()));
        }

        // Validate GSTIN if provided
        validateGstin(updatedVendor.getGstin());

        // Update fields
        existingVendor.setCompanyId(updatedVendor.getCompanyId());
        existingVendor.setVendorName(updatedVendor.getVendorName());
        existingVendor.setGstin(updatedVendor.getGstin());
        existingVendor.setPan(updatedVendor.getPan());
        existingVendor.setTaxTreatment(updatedVendor.getTaxTreatment());
        existingVendor.setContactName(updatedVendor.getContactName());
        existingVendor.setAddressLine1(updatedVendor.getAddressLine1());
        existingVendor.setAddressLine2(updatedVendor.getAddressLine2());
        existingVendor.setCity(updatedVendor.getCity());
        existingVendor.setState(updatedVendor.getState());
        existingVendor.setPinCode(updatedVendor.getPinCode());
        existingVendor.setPhone(updatedVendor.getPhone());
        existingVendor.setMobile(updatedVendor.getMobile());
        existingVendor.setEmail(updatedVendor.getEmail());
        existingVendor.setBankDetails(updatedVendor.getBankDetails());
        return vendorRepository.save(existingVendor);
    }

    public List<VendorModel> getAllVendors() {
        return vendorRepository.findAll();
    }

    public VendorModel getVendorById(String vendorId) {
        return vendorRepository.findByVendorId(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor with ID " + vendorId + " not found"));
    }

    public List<VendorModel> getVendorsByCompanyId(String companyId) {
        // Check if company exists
        companyService.getCompanyById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + companyId));
        return vendorRepository.findAll().stream()
                .filter(v -> companyId.equals(v.getCompanyId()))
                .toList();
    }

    public VendorModel updateVendorCredits(String vendorId, VendorCreditUpdateRequest request) {
        VendorModel existingVendor = vendorRepository.findByVendorId(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor with ID " + vendorId + " not found"));

        // Convert DTO to model
        List<VendorModel.VendorCredit> vendorCredits = null;
        if (request.getVendorCredits() != null) {
            vendorCredits = request.getVendorCredits().stream()
                    .map(dto -> new VendorModel.VendorCredit(
                            dto.getCreditAmount(),
                            dto.getCreditDate(),
                            dto.getCreditDescription()
                    ))
                    .toList();
        }

        // Update vendor credits
        existingVendor.setVendorCredits(vendorCredits);

        // Calculate total credit if vendor credits are not null
        if (vendorCredits != null && !vendorCredits.isEmpty()) {
            BigDecimal totalCredit = vendorCredits.stream()
                    .map(credit -> {
                        try {
                            return new BigDecimal(credit.getCreditAmount());
                        } catch (NumberFormatException e) {
                            return BigDecimal.ZERO;
                        }
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            existingVendor.setTotalCredit(totalCredit);
        } else {
            // Set total credit to null if vendor credits is null or empty
            existingVendor.setTotalCredit(null);
        }

        return vendorRepository.save(existingVendor);
    }
}