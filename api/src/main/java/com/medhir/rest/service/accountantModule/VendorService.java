package com.medhir.rest.service.accountantModule;

import com.medhir.rest.model.accountantModule.VendorModel;
import com.medhir.rest.repository.accountantModule.VendorRepository;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import com.medhir.rest.service.CompanyService;
import com.medhir.rest.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VendorService {

    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;
    @Autowired
    private CompanyService companyService;

    public VendorModel createVendor(VendorModel vendor) {
        // Check if company exists
        companyService.getCompanyById(vendor.getCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + vendor.getCompanyId()));
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
        // Update fields
        existingVendor.setCompanyId(updatedVendor.getCompanyId());
        existingVendor.setVendorName(updatedVendor.getVendorName());
        existingVendor.setCompanyType(updatedVendor.getCompanyType());
        existingVendor.setGstin(updatedVendor.getGstin());
        existingVendor.setPan(updatedVendor.getPan());
        existingVendor.setAddressLine1(updatedVendor.getAddressLine1());
        existingVendor.setAddressLine2(updatedVendor.getAddressLine2());
        existingVendor.setCity(updatedVendor.getCity());
        existingVendor.setState(updatedVendor.getState());
        existingVendor.setCountry(updatedVendor.getCountry());
        existingVendor.setPinCode(updatedVendor.getPinCode());
        existingVendor.setPhone(updatedVendor.getPhone());
        existingVendor.setMobile(updatedVendor.getMobile());
        existingVendor.setEmail(updatedVendor.getEmail());
        existingVendor.setWebsite(updatedVendor.getWebsite());
        existingVendor.setVendorTags(updatedVendor.getVendorTags());
        existingVendor.setBankDetails(updatedVendor.getBankDetails());
        existingVendor.setContactAddresses(updatedVendor.getContactAddresses());
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
} 