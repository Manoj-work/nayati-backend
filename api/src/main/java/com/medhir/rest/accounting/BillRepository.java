package com.medhir.rest.accounting;

import com.medhir.rest.accounting.BillModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BillRepository extends MongoRepository<BillModel, String> {

    // Find all bills by vendorId
    List<BillModel> findByVendorId(String vendorId);

    // Find bills by company name
    List<BillModel> findByCompany(String company);

    // Find bills by status (e.g., Paid, Pending)
    List<BillModel> findByStatus(String status);


    List<BillModel> findByBillDateBetween(Date startDate, Date endDate);

    // Find bills by reverseCharge flag
    List<BillModel> findByReverseCharge(boolean reverseCharge);

    // Find bills by GST treatment
    List<BillModel> findByGstTreatment(String gstTreatment);

    // Find bills by place of supply
    List<BillModel> findByPlaceOfSupply(String placeOfSupply);

    // You can add more custom queries as needed
}
