package com.medhir.rest.controller.settings;

import com.medhir.rest.model.settings.TdsSettingsModel;
import com.medhir.rest.service.settings.TdsSettingsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tds-settings")
public class TdsSettingsController {

    @Autowired
    private TdsSettingsService tdsSettingsService;

    @GetMapping("/company/{companyId}")
    public ResponseEntity<TdsSettingsModel> getTdsSettingsByCompany(@PathVariable String companyId) {
        return ResponseEntity.ok(tdsSettingsService.getTdsSettingsByCompany(companyId));
    }

    @PostMapping
    public ResponseEntity<TdsSettingsModel> createTdsSettings(@Valid @RequestBody TdsSettingsModel tdsSettings) {
        return ResponseEntity.ok(tdsSettingsService.createTdsSettings(tdsSettings));
    }

    @PutMapping("/company/{companyId}")
    public ResponseEntity<TdsSettingsModel> updateTdsSettings(
            @PathVariable String companyId,
            @Valid @RequestBody TdsSettingsModel tdsSettings) {
        return ResponseEntity.ok(tdsSettingsService.updateTdsSettings(companyId, tdsSettings));
    }
}