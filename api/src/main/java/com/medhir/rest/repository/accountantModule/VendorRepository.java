package com.medhir.rest.repository.accountantModule;

import com.medhir.rest.model.accountantModule.VendorModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorRepository extends MongoRepository<VendorModel, String> {
    Optional<VendorModel> findByVendorId(String vendorId);
}
