package com.medhir.rest.controller.accountantModule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medhir.rest.model.accountantModule.BillModel;
import com.medhir.rest.service.accountantModule.BillService;
import com.medhir.rest.dto.BillDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bills")
public class BillController {

    @Autowired
    private BillService billService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> createBill(
            @RequestPart("bill") String billJson,
            @RequestPart(value = "attachment", required = false) MultipartFile attachment) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        BillModel bill = mapper.readValue(billJson, BillModel.class);

        BillModel saved = billService.createBill(bill, attachment);
        return ResponseEntity.ok(Map.of(
                "message", "Bill created successfully"
        ));
    }

    @PutMapping("/{billId}")
    public ResponseEntity<Map<String, Object>> updateBill(
            @PathVariable String billId,
            @RequestPart("bill") @Valid String billJson,
            @RequestPart(value = "attachment", required = false) MultipartFile attachment) throws JsonProcessingException {
       ObjectMapper mapper = new ObjectMapper();
       BillModel bill = mapper.readValue(billJson, BillModel.class);

        BillModel updated = billService.updateBill(billId, bill, attachment);
        return ResponseEntity.ok(Map.of(
                "message", "Bill updated successfully"
//                "bill", updated
        ));
    }

    @GetMapping
    public ResponseEntity<List<BillDTO>> getAllBills() {
        return ResponseEntity.ok(billService.getAllBillDTOs());
    }

    @GetMapping("/{billId}")
    public ResponseEntity<BillDTO> getBillById(@PathVariable String billId) {
        return ResponseEntity.ok(billService.getBillDTOById(billId));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<BillDTO>> getBillsByCompanyId(@PathVariable String companyId) {
        return ResponseEntity.ok(billService.getBillDTOsByCompanyId(companyId));
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<BillDTO>> getBillsByVendorId(@PathVariable String vendorId) {
        return ResponseEntity.ok(billService.getBillDTOsByVendorId(vendorId));
    }
}