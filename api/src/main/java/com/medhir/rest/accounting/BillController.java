package com.medhir.rest.accounting;

import com.medhir.rest.accounting.BillModel;
import com.medhir.rest.accounting.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/accounting/bills")
public class BillController {

    @Autowired
    private BillService billService;

    @PostMapping
    public ResponseEntity<BillModel> createBill(@RequestBody BillModel bill) {
        BillModel createdBill = billService.createBill(bill);
        return ResponseEntity.ok(createdBill);
    }

    @GetMapping
    public ResponseEntity<List<BillModel>> getAllBills() {
        List<BillModel> bills = billService.getAllBills();
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillModel> getBillById(@PathVariable String id) {
        Optional<BillModel> bill = billService.getBillById(id);
        return bill.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillModel> updateBill(@PathVariable String id, @RequestBody BillModel billDetails) {
        try {
            BillModel updatedBill = billService.updateBill(id, billDetails);
            return ResponseEntity.ok(updatedBill);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBill(@PathVariable String id) {
        billService.deleteBill(id);
        return ResponseEntity.noContent().build();
    }
}
