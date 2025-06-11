package com.medhir.rest.repository.settings;

import com.medhir.rest.model.settings.ProfessionalTaxSettingsModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfessionalTaxSettingsRepository extends MongoRepository<ProfessionalTaxSettingsModel, String> {
    Optional<ProfessionalTaxSettingsModel> findFirstByCompanyIdOrderByCreatedAtDesc(String companyId);
}