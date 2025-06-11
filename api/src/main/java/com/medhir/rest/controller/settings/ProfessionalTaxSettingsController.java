package com.medhir.rest.controller.settings;

import com.medhir.rest.model.settings.ProfessionalTaxSettingsModel;
import com.medhir.rest.service.settings.ProfessionalTaxSettingsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/professional-tax-settings")
public class ProfessionalTaxSettingsController {

    @Autowired
    private ProfessionalTaxSettingsService professionalTaxSettingsService;

    @GetMapping("/company/{companyId}")
    public ResponseEntity<ProfessionalTaxSettingsModel> getProfessionalTaxSettingsByCompany(@PathVariable String companyId) {
        return ResponseEntity.ok(professionalTaxSettingsService.getProfessionalTaxSettingsByCompany(companyId));
    }

    @PostMapping
    public ResponseEntity<ProfessionalTaxSettingsModel> createProfessionalTaxSettings(
            @Valid @RequestBody ProfessionalTaxSettingsModel settings) {
        return ResponseEntity.ok(professionalTaxSettingsService.createProfessionalTaxSettings(settings));
    }

    @PutMapping("/company/{companyId}")
    public ResponseEntity<ProfessionalTaxSettingsModel> updateProfessionalTaxSettings(
            @PathVariable String companyId,
            @Valid @RequestBody ProfessionalTaxSettingsModel settings) {
        return ResponseEntity.ok(professionalTaxSettingsService.updateProfessionalTaxSettings(companyId, settings));
    }
}