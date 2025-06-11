package com.medhir.rest.service.settings;

import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.model.settings.ProfessionalTaxSettingsModel;
import com.medhir.rest.repository.settings.ProfessionalTaxSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProfessionalTaxSettingsService {

    @Autowired
    private ProfessionalTaxSettingsRepository repository;

    public ProfessionalTaxSettingsModel getProfessionalTaxSettingsByCompany(String companyId) {
        return repository.findFirstByCompanyIdOrderByCreatedAtDesc(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Professional tax settings not found for company: " + companyId));
    }

    public ProfessionalTaxSettingsModel createProfessionalTaxSettings(ProfessionalTaxSettingsModel settings) {
        // Check if settings already exist for the company
        Optional<ProfessionalTaxSettingsModel> existingSettings = repository.findFirstByCompanyIdOrderByCreatedAtDesc(settings.getCompanyId());
        if (existingSettings.isPresent()) {
            throw new DuplicateResourceException("Professional tax settings already exist for this company. Use update endpoint to modify.");
        }

        // Validate settings
        validateProfessionalTaxSettings(settings);

        // Set timestamps
        settings.setCreatedAt(LocalDateTime.now().toString());
        settings.setUpdatedAt(LocalDateTime.now().toString());

        return repository.save(settings);
    }

    public ProfessionalTaxSettingsModel updateProfessionalTaxSettings(String companyId, ProfessionalTaxSettingsModel settings) {
        // Get existing settings for the company
        ProfessionalTaxSettingsModel existingSettings = repository.findFirstByCompanyIdOrderByCreatedAtDesc(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Professional tax settings not found for company: " + companyId));

        // Set the company ID in the settings object
        settings.setCompanyId(companyId);

        // Validate settings
        validateProfessionalTaxSettings(settings);

        // Update fields
        existingSettings.setMonthlySalaryThreshold(settings.getMonthlySalaryThreshold());
        existingSettings.setAmountAboveThreshold(settings.getAmountAboveThreshold());
        existingSettings.setAmountBelowThreshold(settings.getAmountBelowThreshold());
        existingSettings.setDescription(settings.getDescription());
        existingSettings.setUpdatedAt(LocalDateTime.now().toString());

        return repository.save(existingSettings);
    }

    private void validateProfessionalTaxSettings(ProfessionalTaxSettingsModel settings) {
        if (settings.getMonthlySalaryThreshold() == null || settings.getMonthlySalaryThreshold() <= 0) {
            throw new ResourceNotFoundException("Monthly salary threshold must be a positive number");
        }

        if (settings.getAmountAboveThreshold() == null || settings.getAmountAboveThreshold() < 0) {
            throw new ResourceNotFoundException("Amount above threshold cannot be negative");
        }

        if (settings.getAmountBelowThreshold() == null || settings.getAmountBelowThreshold() < 0) {
            throw new ResourceNotFoundException("Amount below threshold cannot be negative");
        }

        if (settings.getDescription() == null || settings.getDescription().trim().isEmpty()) {
            throw new ResourceNotFoundException("Description cannot be empty");
        }
    }
}