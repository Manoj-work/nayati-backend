package com.medhir.rest.repository.settings;

import com.medhir.rest.model.settings.TdsSettingsModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TdsSettingsRepository extends MongoRepository<TdsSettingsModel, String> {
    Optional<TdsSettingsModel> findFirstByCompanyIdOrderByCreatedAtDesc(String companyId);
}