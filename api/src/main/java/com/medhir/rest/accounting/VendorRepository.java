package com.medhir.rest.accounting;

import com.medhir.rest.accounting.VendorModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends MongoRepository<VendorModel, String> {

    // Find vendor by exact vendorName (unique)
    Optional<VendorModel> findByVendorName(String vendorName);



    // Find vendor by GSTIN (unique, if provided)
    Optional<VendorModel> findByGstin(String gstin);

    // Find vendor by PAN (unique, if provided)
    Optional<VendorModel> findByPan(String pan);

    // Find vendors by city
    List<VendorModel> findByCity(String city);

    // Find vendors by state
    List<VendorModel> findByState(String state);

    // Find vendors by vendor tags (match any tag in the list)
    List<VendorModel> findByVendorTagsIn(List<String> tags);

    // Check existence by vendorName
    boolean existsByVendorName(String vendorName);

    // Check existence by GSTIN
    boolean existsByGstin(String gstin);

    // Check existence by PAN
    boolean existsByPan(String pan);
}
