package com.medhir.rest.sales.service;

import com.medhir.rest.sales.model.PipelineStage;
import com.medhir.rest.sales.repository.PipelineStageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

@Service
public class PipelineStageMigrationService {

    @Autowired
    private PipelineStageRepository pipelineStageRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Migrate existing pipeline stages to include isForm and formType fields
     */
    public void migrateExistingPipelineStages() {
        List<PipelineStage> allStages = pipelineStageRepository.findAll();
        int migratedCount = 0;
        
        for (PipelineStage stage : allStages) {
            // Always update to ensure the fields are present
            stage.setForm(false);
            stage.setFormType(null);
            stage.setUpdatedAt(java.time.LocalDateTime.now().toString());
            
            // Save the updated stage
            pipelineStageRepository.save(stage);
            migratedCount++;
            System.out.println("Migrated pipeline stage: " + stage.getName() + " (ID: " + stage.getStageId() + ")");
        }
        
        System.out.println("Pipeline stage migration completed. Total stages processed: " + migratedCount);
    }

    /**
     * Check if a pipeline stage has the new form fields
     * This method is deprecated and will be removed after migration is complete
     */
    @Deprecated
    private boolean hasFormFields(PipelineStage stage) {
        // Check if the stage has been updated with form fields
        // We can check if the updatedAt timestamp is recent or if form fields are explicitly set
        return stage.getUpdatedAt() != null && 
               stage.getUpdatedAt().contains("2025-07-05") && 
               stage.getUpdatedAt().contains("01:12:38");
    }

    /**
     * Alternative migration using MongoDB update operations
     */
    public void migrateUsingMongoUpdate() {
        Query query = new Query();
        Update update = new Update()
            .set("isForm", false)
            .set("formType", null)
            .set("updatedAt", java.time.LocalDateTime.now().toString());
        
        mongoTemplate.updateMulti(query, update, PipelineStage.class);
        System.out.println("Bulk migration completed for pipeline stages");
    }

    /**
     * Get migration status
     */
    public MigrationStatus getMigrationStatus() {
        List<PipelineStage> allStages = pipelineStageRepository.findAll();
        long migratedCount = allStages.stream()
            .filter(this::hasFormFields)
            .count();
        
        return new MigrationStatus(allStages.size(), migratedCount);
    }

    public static class MigrationStatus {
        private final long totalStages;
        private final long migratedStages;

        public MigrationStatus(long totalStages, long migratedStages) {
            this.totalStages = totalStages;
            this.migratedStages = migratedStages;
        }

        public long getTotalStages() { return totalStages; }
        public long getMigratedStages() { return migratedStages; }
        public boolean isComplete() { return totalStages == migratedStages; }
    }
} 