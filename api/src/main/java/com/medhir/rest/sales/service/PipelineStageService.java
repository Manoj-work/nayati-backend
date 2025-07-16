package com.medhir.rest.sales.service;

import com.medhir.rest.sales.model.PipelineStage;
import com.medhir.rest.sales.model.FormType;
import com.medhir.rest.sales.repository.PipelineStageRepository;
import com.medhir.rest.sales.repository.LeadRepository;
import com.medhir.rest.sales.dto.pipeline.CreatePipelineStageRequest;
import com.medhir.rest.sales.dto.pipeline.UpdatePipelineStageRequest;
import com.medhir.rest.sales.dto.pipeline.ReorderPipelineStageRequest;
import com.medhir.rest.sales.dto.pipeline.PipelineStageResponse;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PipelineStageService {

    @Autowired
    private PipelineStageRepository pipelineStageRepository;

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    //  Get all active pipeline stages
    public List<PipelineStageResponse> getAllActiveStages() {
        List<PipelineStage> stages = pipelineStageRepository.findByIsActiveTrueOrderByOrderIndexAsc();
        return stages.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    //  Get all pipeline stages (including inactive)
    public List<PipelineStageResponse> getAllStages() {
        List<PipelineStage> stages = pipelineStageRepository.findAll();
        return stages.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    //  Get stage by ID
    public PipelineStageResponse getStageById(String id) {
        PipelineStage stage = pipelineStageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pipeline stage not found with id: " + id));
        return mapToResponse(stage);
    }

    // 🎯 Create new pipeline stage
    public PipelineStageResponse createStage(CreatePipelineStageRequest request, String createdBy) {
        validateStageNameUnique(request.getName());
        validateFormTypeConfiguration(request.isForm(), request.getFormType());
        int nextOrderIndex = getNextOrderIndex();
        PipelineStage stage = new PipelineStage();
        assignCreateFields(stage, request, createdBy, nextOrderIndex);
        stage.setStageId("STAGE-" + snowflakeIdGenerator.nextId());
        PipelineStage savedStage = pipelineStageRepository.save(stage);
        return mapToResponse(savedStage);
    }

    //  Update pipeline stage
    public PipelineStageResponse updateStage(String id, UpdatePipelineStageRequest request, String updatedBy) {
        PipelineStage stage = getStageOrThrow(id);
        if (request.getName() != null && !request.getName().equalsIgnoreCase(stage.getName())) {
            validateStageNameUnique(request.getName());
        }
        if (request.getIsForm() != null) {
            validateFormTypeConfiguration(request.getIsForm(), request.getFormType());
        }
        assignUpdateFields(stage, request);
        PipelineStage savedStage = pipelineStageRepository.save(stage);
        return mapToResponse(savedStage);
    }

    //  Delete pipeline stage
    public void deleteStage(String stageId) {
        PipelineStage stage = pipelineStageRepository.findByStageId(stageId)
            .orElseThrow(() -> new RuntimeException("Pipeline stage not found with stageId: " + stageId));
        long leadCount = leadRepository.countByStageId(stage.getStageId());
        if (leadCount > 0) {
            throw new RuntimeException("Cannot delete stage '" + stage.getName() + "' because " + leadCount + " lead(s) are currently in this stage. Please move them to another stage first.");
        }
        pipelineStageRepository.deleteById(stage.getId());
    }

    //  Reorder pipeline stages
    public List<PipelineStageResponse> reorderStages(List<ReorderPipelineStageRequest> reorderRequests) {
        for (ReorderPipelineStageRequest request : reorderRequests) {
            PipelineStage stage = pipelineStageRepository.findById(request.getStageId())
                    .orElseThrow(() -> new RuntimeException("Pipeline stage not found with id: " + request.getStageId()));
            
            stage.setOrderIndex(request.getNewOrderIndex());
            stage.setUpdatedAt(java.time.LocalDateTime.now().toString());
            pipelineStageRepository.save(stage);
        }

        return getAllActiveStages();
    }

    //  Get stage names as list (for frontend compatibility)
    public List<String> getStageNames() {
        List<PipelineStage> stages = pipelineStageRepository.findByIsActiveTrueOrderByOrderIndexAsc();
        return stages.stream()
                .map(PipelineStage::getName)
                .collect(Collectors.toList());
    }

    //  Validate stage exists by name (for backward compatibility)
    public boolean stageExists(String stageName) {
        return pipelineStageRepository.existsByNameIgnoreCase(stageName);
    }

    // Validate stage exists by ID (new method for stageId approach)
    public boolean stageExistsById(String stageId) {
        return pipelineStageRepository.existsByStageId(stageId);
    }

    //  Get stage by name (for backward compatibility)
    public Optional<PipelineStage> getStageByName(String stageName) {
        return pipelineStageRepository.findByNameIgnoreCase(stageName);
    }

    //  Get stage by ID (new method for stageId approach)
    public Optional<PipelineStage> getStageByIdOptional(String stageId) {
        return pipelineStageRepository.findByStageId(stageId);
    }

    //  Initialize default stages (for first-time setup)
    public void initializeDefaultStages(String createdBy) {
        if (pipelineStageRepository.count() > 0) {
            return; // Already initialized
        }

        // Updated default stages and their properties
        String[] defaultStages = {"New", "Contacted", "Qualified", "Quoted", "Converted", "Lost", "Junk"};
        String[] colors = {"#3B82F6", "#10B981", "#F59E0B", "#8B5CF6", "#EF4444", "#6B7280", "#F87171"};

        for (int i = 0; i < defaultStages.length; i++) {
            String stageName = defaultStages[i];
            boolean isForm = false;
            FormType formType = null;
            if ("Converted".equals(stageName)) {
                isForm = true;
                formType = FormType.CONVERTED;
            } else if ("Lost".equals(stageName)) {
                isForm = true;
                formType = FormType.LOST;
            } else if ("Junk".equals(stageName)) {
                isForm = true;
                formType = FormType.JUNK;
            }
            PipelineStage stage = new PipelineStage(
                stageName,
                "Default " + stageName + " stage",
                i,
                colors[i],
                isForm,
                formType,
                createdBy
            );
            stage.setStageId("STAGE-" + snowflakeIdGenerator.nextId());
            pipelineStageRepository.save(stage);
        }
    }

    //  Check if migration is needed
    public boolean isMigrationNeeded() {
        List<PipelineStage> allStages = pipelineStageRepository.findAll();
        return allStages.stream().anyMatch(stage -> stage.getFormType() == null);
    }

    //  Get migration status
    public MigrationStatus getMigrationStatus() {
        List<PipelineStage> allStages = pipelineStageRepository.findAll();
        long migratedCount = allStages.stream()
            .filter(stage -> stage.getFormType() != null || stage.isForm())
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

    //  Helper method to get next order index
    private int getNextOrderIndex() {
        Optional<PipelineStage> lastStage = pipelineStageRepository.findTopByOrderByOrderIndexDesc();
        return lastStage.map(stage -> stage.getOrderIndex() + 1).orElse(0);
    }

    //  Helper method to map to response DTO
    private PipelineStageResponse mapToResponse(PipelineStage stage) {
        PipelineStageResponse response = new PipelineStageResponse();
        response.setStageId(stage.getStageId());
        response.setName(stage.getName());
        response.setDescription(stage.getDescription());
        response.setOrderIndex(stage.getOrderIndex());
        response.setColor(stage.getColor());
        response.setActive(stage.isActive());
        response.setForm(stage.isForm());
        response.setFormType(stage.getFormType());
        response.setCreatedBy(stage.getCreatedBy());
        response.setCreatedAt(stage.getCreatedAt());
        response.setUpdatedAt(stage.getUpdatedAt());
        // Count leads in this stage using stageId (Snowflake ID)
        long leadCount = leadRepository.countByStageId(stage.getStageId());
        response.setLeadCount((int) leadCount);
        return response;
    }

    // Get stage by stageId (Snowflake ID)
    public PipelineStageResponse getStageByStageId(String stageId) {
        PipelineStage stage = pipelineStageRepository.findByStageId(stageId)
                .orElseThrow(() -> new RuntimeException("Pipeline stage not found with stageId: " + stageId));
        return mapToResponse(stage);
    }

    // Helper: Validate stage name uniqueness
    private void validateStageNameUnique(String name) {
        if (pipelineStageRepository.existsByNameIgnoreCase(name)) {
            throw new RuntimeException("Pipeline stage with name '" + name + "' already exists");
        }
    }

    // Helper: Validate stage exists by ID
    private PipelineStage getStageOrThrow(String id) {
        return pipelineStageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pipeline stage not found with id: " + id));
    }

    // Helper: Validate form type configuration
    private void validateFormTypeConfiguration(boolean isForm, FormType formType) {
        if (isForm && formType == null) {
            throw new RuntimeException("Form type is required when isForm is true");
        }
        if (!isForm && formType != null) {
            throw new RuntimeException("Form type should not be provided when isForm is false");
        }
    }

    // Helper: Assign fields from CreatePipelineStageRequest to PipelineStage
    private void assignCreateFields(PipelineStage stage, CreatePipelineStageRequest request, String createdBy, int orderIndex) {
        stage.setName(request.getName());
        stage.setDescription(request.getDescription());
        stage.setOrderIndex(orderIndex);
        stage.setColor(request.getColor() != null ? request.getColor() : "#3B82F6");
        stage.setActive(true);
        stage.setForm(request.isForm());
        stage.setFormType(request.getFormType());
        stage.setCreatedBy(createdBy);
        stage.setCreatedAt(java.time.LocalDateTime.now().toString());
        stage.setUpdatedAt(java.time.LocalDateTime.now().toString());
    }

    // Helper: Assign fields from UpdatePipelineStageRequest to PipelineStage
    private void assignUpdateFields(PipelineStage stage, UpdatePipelineStageRequest request) {
        if (request.getName() != null) {
            stage.setName(request.getName());
        }
        if (request.getDescription() != null) {
            stage.setDescription(request.getDescription());
        }
        if (request.getColor() != null) {
            stage.setColor(request.getColor());
        }
        if (request.getIsActive() != null) {
            stage.setActive(request.getIsActive());
        }
        if (request.getIsForm() != null) {
            stage.setForm(request.getIsForm());
        }
        if (request.getFormType() != null) {
            stage.setFormType(request.getFormType());
        }
        stage.setUpdatedAt(java.time.LocalDateTime.now().toString());
    }
} 