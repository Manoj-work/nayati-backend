package com.medhir.rest.controller.company;

import com.medhir.rest.dto.company.CompanyResponseDTO;
import com.medhir.rest.dto.company.CreateCompanyWithHeadRequest;
import com.medhir.rest.model.company.CompanyModel;
import com.medhir.rest.service.company.CompanyOrchestratorService;
import com.medhir.rest.service.company.CompanyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/superadmin/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;
    @Autowired
    private CompanyOrchestratorService companyOrchestratorService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCompany(@Valid @RequestBody CompanyModel company) {
        CompanyModel savedCompany = companyService.createCompany(company);
        return ResponseEntity.ok(Map.of(
                "message", "Company created successfully!"
//                "company", savedCompany
        ));
    }

    @GetMapping
    public List<CompanyResponseDTO> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyResponseDTO> getCompanyById(@PathVariable String companyId) {
        CompanyResponseDTO company = companyService.getCompanywithHeaddetailsById(companyId);
        return ResponseEntity.ok(company);
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<Map<String, Object>> updateCompany(@PathVariable String companyId, @Valid @RequestBody CompanyModel company) {
        CompanyModel updatedCompany =  companyService.updateCompany(companyId, company);
        return ResponseEntity.ok(Map.of(
                "message", "Company updated successfully!"
//                "Company ",updatedCompany
        ));
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<Map<String, String>> deleteCompany(@PathVariable String companyId) {
        companyService.deleteCompany(companyId);
        return ResponseEntity.ok(Map.of("message", "Company deleted successfully!"));
    }

    @PostMapping("/with-head")
    public ResponseEntity<CompanyModel> createCompanyWithHead(
            @RequestBody @Valid CreateCompanyWithHeadRequest request) {
        return ResponseEntity.ok(companyOrchestratorService.createCompanyWithHead(request));
    }

}
