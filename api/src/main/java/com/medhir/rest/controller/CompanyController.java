package com.medhir.rest.controller;

import com.medhir.rest.model.CompanyModel;
import com.medhir.rest.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/superadmin/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping
    public ResponseEntity<CompanyModel> createCompany(@Valid @RequestBody CompanyModel company) {
        CompanyModel savedCompany = companyService.createCompanyWithHead(company);
        return ResponseEntity.ok(savedCompany);
    }

    @GetMapping
    public List<CompanyModel> getAllCompanies() {
        return companyService.getAllCompanies();
    }
    
    @GetMapping("/{companyId}")
    public ResponseEntity<Optional<CompanyModel>> getCompanyById(@PathVariable String companyId) {
        Optional<CompanyModel> company = companyService.getCompanyById(companyId);
        return ResponseEntity.ok(company);
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyModel> updateCompany(@PathVariable String companyId, @Valid @RequestBody CompanyModel company) {
        CompanyModel updatedCompany =  companyService.updateCompany(companyId, company);
        return ResponseEntity.ok(updatedCompany);
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<Map<String, String>> deleteCompany(@PathVariable String companyId) {
        companyService.deleteCompany(companyId);
        return ResponseEntity.ok(Map.of("message", "Company deleted successfully!"));
    }
}
