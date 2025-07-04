package com.medhir.rest.sales.service;

import com.medhir.rest.sales.model.PipelineStage;
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

    // 🎯 Get all active pipeline stages
    public List<PipelineStageResponse> getAllActiveStages() {
        List<PipelineStage> stages = pipelineStageRepository.findByIsActiveTrueOrderByOrderIndexAsc();
        return stages.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 🎯 Get all pipeline stages (including inactive)
    public List<PipelineStageResponse> getAllStages() {
        List<PipelineStage> stages = pipelineStageRepository.findAll();
        return stages.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 🎯 Get stage by ID
    public PipelineStageResponse getStageById(String id) {
        PipelineStage stage = pipelineStageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pipeline stage not found with id: " + id));
        return mapToResponse(stage);
    }

    // 🎯 Create new pipeline stage
    public PipelineStageResponse createStage(CreatePipelineStageRequest request, String createdBy) {
        validateStageNameUnique(request.getName());
        int nextOrderIndex = getNextOrderIndex();
        PipelineStage stage = new PipelineStage();
        assignCreateFields(stage, request, createdBy, nextOrderIndex);
        stage.setStageId("STAGE-" + snowflakeIdGenerator.nextId());
        PipelineStage savedStage = pipelineStageRepository.save(stage);
        return mapToResponse(savedStage);
    }

    // 🎯 Update pipeline stage
    public PipelineStageResponse updateStage(String id, UpdatePipelineStageRequest request, String updatedBy) {
        PipelineStage stage = getStageOrThrow(id);
        if (request.getName() != null && !request.getName().equalsIgnoreCase(stage.getName())) {
            validateStageNameUnique(request.getName());
            }
        assignUpdateFields(stage, request);
        PipelineStage savedStage = pipelineStageRepository.save(stage);
        return mapToResponse(savedStage);
    }

    // 🎯 Delete pipeline stage
    public void deleteStage(String stageId) {
        PipelineStage stage = pipelineStageRepository.findByStageId(stageId)
            .orElseThrow(() -> new RuntimeException("Pipeline stage not found with stageId: " + stageId));
        long leadCount = leadRepository.countByStageId(stage.getStageId());
        if (leadCount > 0) {
            throw new RuntimeException("Cannot delete stage '" + stage.getName() + "' because " + leadCount + " lead(s) are currently in this stage. Please move them to another stage first.");
        }
        pipelineStageRepository.deleteById(stage.getId());
    }

    // 🎯 Reorder pipeline stages
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

    // 🎯 Get stage names as list (for frontend compatibility)
    public List<String> getStageNames() {
        List<PipelineStage> stages = pipelineStageRepository.findByIsActiveTrueOrderByOrderIndexAsc();
        return stages.stream()
                .map(PipelineStage::getName)
                .collect(Collectors.toList());
    }

    // 🎯 Validate stage exists by name (for backward compatibility)
    public boolean stageExists(String stageName) {
        return pipelineStageRepository.existsByNameIgnoreCase(stageName);
    }

    // 🎯 Validate stage exists by ID (new method for stageId approach)
    public boolean stageExistsById(String stageId) {
        return pipelineStageRepository.existsByStageId(stageId);
    }

    // 🎯 Get stage by name (for backward compatibility)
    public Optional<PipelineStage> getStageByName(String stageName) {
        return pipelineStageRepository.findByNameIgnoreCase(stageName);
    }

    // 🎯 Get stage by ID (new method for stageId approach)
    public Optional<PipelineStage> getStageByIdOptional(String stageId) {
        return pipelineStageRepository.findById(stageId);
    }

    // 🎯 Initialize default stages (for first-time setup)
    public void initializeDefaultStages(String createdBy) {
        if (pipelineStageRepository.count() > 0) {
            return; // Already initialized
        }

        String[] defaultStages = {"New", "Contacted", "Qualified", "Quoted", "Converted", "Lost"};
        String[] colors = {"#3B82F6", "#10B981", "#F59E0B", "#8B5CF6", "#EF4444", "#6B7280"};

        for (int i = 0; i < defaultStages.length; i++) {
            PipelineStage stage = new PipelineStage(
                    defaultStages[i],
                    "Default " + defaultStages[i] + " stage",
                    i,
                    colors[i],
                    createdBy
            );
            stage.setStageId("STAGE-" + snowflakeIdGenerator.nextId());
            pipelineStageRepository.save(stage);
        }
    }

    // 🎯 Helper method to get next order index
    private int getNextOrderIndex() {
        Optional<PipelineStage> lastStage = pipelineStageRepository.findTopByOrderByOrderIndexDesc();
        return lastStage.map(stage -> stage.getOrderIndex() + 1).orElse(0);
    }

    // 🎯 Helper method to map to response DTO
    private PipelineStageResponse mapToResponse(PipelineStage stage) {
        PipelineStageResponse response = new PipelineStageResponse();
        response.setStageId(stage.getStageId());
        response.setName(stage.getName());
        response.setDescription(stage.getDescription());
        response.setOrderIndex(stage.getOrderIndex());
        response.setColor(stage.getColor());
        response.setActive(stage.isActive());
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

    // Helper: Assign fields from CreatePipelineStageRequest to PipelineStage
    private void assignCreateFields(PipelineStage stage, CreatePipelineStageRequest request, String createdBy, int orderIndex) {
        stage.setName(request.getName());
        stage.setDescription(request.getDescription());
        stage.setOrderIndex(orderIndex);
        stage.setColor(request.getColor() != null ? request.getColor() : "#3B82F6");
        stage.setActive(true);
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
        stage.setUpdatedAt(java.time.LocalDateTime.now().toString());
    }
} 