package com.medhir.rest.accounting;

import com.medhir.rest.accounting.VendorModel;
import com.medhir.rest.accounting.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/accountant/vendors")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    @PostMapping
    public ResponseEntity<VendorModel> createVendor(@Valid @RequestBody VendorModel vendor) {
        VendorModel createdVendor = vendorService.createVendor(vendor);
        return ResponseEntity.ok(createdVendor);
    }

    @GetMapping
    public ResponseEntity<List<VendorModel>> getAllVendors() {
        List<VendorModel> vendors = vendorService.getAllVendors();
        return ResponseEntity.ok(vendors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendorModel> getVendorById(@PathVariable String id) {
        return vendorService.getVendorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<VendorModel> updateVendor(@PathVariable String id, @Valid @RequestBody VendorModel vendor) {
        try {
            VendorModel updatedVendor = vendorService.updateVendor(id, vendor);
            return ResponseEntity.ok(updatedVendor);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVendor(@PathVariable String id) {
        try {
            vendorService.deleteVendor(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
