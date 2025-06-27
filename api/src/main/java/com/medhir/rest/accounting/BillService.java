package com.medhir.rest.accounting;

import com.medhir.rest.accounting.BillModel;
import com.medhir.rest.accounting.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    public BillModel createBill(BillModel bill) {
        return billRepository.save(bill);
    }

    public List<BillModel> getAllBills() {
        return billRepository.findAll();
    }

    public Optional<BillModel> getBillById(String id) {
        return billRepository.findById(id);
    }

    public BillModel updateBill(String id, BillModel billDetails) {
        BillModel bill = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found with id: " + id));

        // Update fields
        bill.setVendorId(billDetails.getVendorId());
        bill.setBillReference(billDetails.getBillReference());
        bill.setBillDate(billDetails.getBillDate());
        bill.setDueDate(billDetails.getDueDate());
        bill.setPlaceOfSupply(billDetails.getPlaceOfSupply());
        bill.setGstTreatment(billDetails.getGstTreatment());
        bill.setReverseCharge(billDetails.isReverseCharge());
        bill.setCompany(billDetails.getCompany());
        bill.setJournal(billDetails.getJournal());
        bill.setCurrency(billDetails.getCurrency());
        bill.setStatus(billDetails.getStatus());
        bill.setTotalAmount(billDetails.getTotalAmount());
        bill.setBillLines(billDetails.getBillLines());

        return billRepository.save(bill);
    }

    public void deleteBill(String id) {
        billRepository.deleteById(id);
    }
}
