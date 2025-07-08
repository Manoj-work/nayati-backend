package com.medhir.rest.sales.controller;

import com.medhir.rest.sales.service.PipelineStageService;
import com.medhir.rest.sales.service.PipelineStageMigrationService;
import com.medhir.rest.sales.dto.pipeline.CreatePipelineStageRequest;
import com.medhir.rest.sales.dto.pipeline.UpdatePipelineStageRequest;
import com.medhir.rest.sales.dto.pipeline.ReorderPipelineStageRequest;
import com.medhir.rest.sales.dto.pipeline.PipelineStageResponse;
import com.medhir.rest.sales.dto.pipeline.FormTypeDTO;
import com.medhir.rest.sales.model.FormType;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pipeline-stages")
@CrossOrigin
public class PipelineStageController {

    @Autowired
    private PipelineStageService pipelineStageService;

    @Autowired
    private PipelineStageMigrationService migrationService;

    // 🎯 Get all active pipeline stages
    @GetMapping
    public List<PipelineStageResponse> getAllActiveStages() {
        return pipelineStageService.getAllActiveStages();
    }

    // 🎯 Get all pipeline stages (including inactive)
    @GetMapping("/all")
    public List<PipelineStageResponse> getAllStages() {
        return pipelineStageService.getAllStages();
    }

    // 🎯 Get stage names as list (for frontend compatibility)
    @GetMapping("/names")
    public List<String> getStageNames() {
        return pipelineStageService.getStageNames();
    }

    // 🎯 Get stage by ID
    @GetMapping("/{stageId}")
    public PipelineStageResponse getStageById(@PathVariable String stageId) {
        return pipelineStageService.getStageByStageId(stageId);
    }

    // 🎯 Create new pipeline stage
    @PostMapping
    public PipelineStageResponse createStage(@Valid @RequestBody CreatePipelineStageRequest request) {
        return pipelineStageService.createStage(request, "Public User");
    }

    // 🎯 Update pipeline stage
    @PutMapping("/{stageId}")
    public PipelineStageResponse updateStage(@PathVariable String stageId, @Valid @RequestBody UpdatePipelineStageRequest request) {
        return pipelineStageService.updateStage(stageId, request, "Public User");
    }

    // 🎯 Delete pipeline stage
    @DeleteMapping("/{stageId}")
    public void deleteStage(@PathVariable String stageId) {
        pipelineStageService.deleteStage(stageId);
    }

    // 🎯 Reorder pipeline stages
    @PostMapping("/reorder")
    public List<PipelineStageResponse> reorderStages(@Valid @RequestBody List<ReorderPipelineStageRequest> reorderRequests) {
        return pipelineStageService.reorderStages(reorderRequests);
    }

    // 🎯 Initialize default stages (for first-time setup)
    @PostMapping("/initialize")
    public List<PipelineStageResponse> initializeDefaultStages() {
        pipelineStageService.initializeDefaultStages("Public User");
        return pipelineStageService.getAllActiveStages();
    }

    // 🎯 Check if stage exists
    @GetMapping("/exists/{stageName}")
    public boolean stageExists(@PathVariable String stageName) {
        return pipelineStageService.stageExists(stageName);
    }

    // 🎯 Get available form types
    @GetMapping("/form-types")
    public List<FormTypeDTO> getAvailableFormTypes() {
        return Arrays.stream(FormType.values())
                .map(FormTypeDTO::fromFormType)
                .collect(Collectors.toList());
    }

    // 🎯 Migration endpoints
    @PostMapping("/migrate")
    public String migratePipelineStages() {
        migrationService.migrateExistingPipelineStages();
        return "Migration completed successfully";
    }

    @GetMapping("/migration-status")
    public PipelineStageMigrationService.MigrationStatus getMigrationStatus() {
        return migrationService.getMigrationStatus();
    }
} 