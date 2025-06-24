package com.medhir.rest.repository.accountantModule;

import com.medhir.rest.model.accountantModule.BillModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends MongoRepository<BillModel, String> {
    List<BillModel> findByCompanyId(String companyId);
    List<BillModel> findByVendorId(String vendorId);
    Optional<BillModel> findByBillId(String billId);
}