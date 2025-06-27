package com.medhir.rest.accounting;

import com.medhir.rest.accounting.VendorModel;
import com.medhir.rest.accounting.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VendorService {


    private final VendorRepository vendorRepository;



    public VendorModel createVendor(VendorModel vendor) {
        return vendorRepository.save(vendor);
    }

    public List<VendorModel> getAllVendors() {
        return vendorRepository.findAll();
    }

    public Optional<VendorModel> getVendorById(String id) {
        return vendorRepository.findById(id);
    }

    public VendorModel updateVendor(String id, VendorModel vendorDetails) {
        VendorModel existingVendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found with id: " + id));

        // Update fields - example for some fields; update all required fields similarly
        existingVendor.setVendorName(vendorDetails.getVendorName());
        existingVendor.setCompanyOrIndividual(vendorDetails.getCompanyOrIndividual());
        existingVendor.setGstin(vendorDetails.getGstin());
        existingVendor.setPan(vendorDetails.getPan());
        existingVendor.setAddressLine1(vendorDetails.getAddressLine1());
        existingVendor.setAddressLine2(vendorDetails.getAddressLine2());
        existingVendor.setCity(vendorDetails.getCity());
        existingVendor.setState(vendorDetails.getState());
        existingVendor.setPinCode(vendorDetails.getPinCode());
        existingVendor.setCountry(vendorDetails.getCountry());
        existingVendor.setPhone(vendorDetails.getPhone());
        existingVendor.setMobile(vendorDetails.getMobile());
        existingVendor.setEmail(vendorDetails.getEmail());
        existingVendor.setWebsite(vendorDetails.getWebsite());
        existingVendor.setVendorTags(vendorDetails.getVendorTags());
        existingVendor.setContacts(vendorDetails.getContacts());
        existingVendor.setBankingDetails(vendorDetails.getBankingDetails());

        return vendorRepository.save(existingVendor);
    }

    public void deleteVendor(String id) {
        if (!vendorRepository.existsById(id)) {
            throw new RuntimeException("Vendor not found with id: " + id);
        }
        vendorRepository.deleteById(id);
    }
}
