package com.medhir.rest.controller.accountantModule;

import com.medhir.rest.model.accountantModule.VendorModel;
import com.medhir.rest.service.accountantModule.VendorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/vendors")
//@Tag(name = "Vendor API", description = "Endpoints for Vendor operations")
public class VendorController {

    @Autowired
    private VendorService vendorService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createVendor(@Valid @RequestBody VendorModel vendor) {
        VendorModel savedVendor = vendorService.createVendor(vendor);
        return ResponseEntity.ok(Map.of(
                "message", "Vendor created successfully!"
//                "vendor", savedVendor
        ));
    }

    @GetMapping
    public ResponseEntity<List<VendorModel>> getAllVendors() {
        return ResponseEntity.ok(vendorService.getAllVendors());
    }

    @GetMapping("/{vendorId}")
    public ResponseEntity<VendorModel> getVendorById(@PathVariable String vendorId) {
        return ResponseEntity.ok(vendorService.getVendorById(vendorId));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<VendorModel>> getVendorsByCompanyId(@PathVariable String companyId) {
        return ResponseEntity.ok(vendorService.getVendorsByCompanyId(companyId));
    }

    @PutMapping("/{vendorId}")
    public ResponseEntity<Map<String, Object>> updateVendor(@PathVariable String vendorId,
                                                            @Valid @RequestBody VendorModel vendor) {
        VendorModel updated = vendorService.updateVendor(vendorId, vendor);
        return ResponseEntity.ok(Map.of(
                "message", "Vendor updated successfully!"
//                "vendor", updated
        ));
    }
}
