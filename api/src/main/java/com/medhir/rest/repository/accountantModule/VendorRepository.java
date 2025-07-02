package com.medhir.rest.repository.accountantModule;

import com.medhir.rest.model.accountantModule.VendorModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface VendorRepository extends MongoRepository<VendorModel, String> {
    Optional<VendorModel> findByVendorId(String vendorId);

    List<VendorModel> findByVendorIdIn(Set<String> vendorIds);
}
