package com.medhir.rest.sales;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pipeline/stages")
public class PipelineStageController {

    @Autowired
    private PipelineStageService stageService;

    @PostMapping
    public ResponseEntity<PipelineStageModel> createStage(@RequestBody PipelineStageModel stage) {
        return ResponseEntity.ok(stageService.createStage(stage));
    }

    @GetMapping("/{stageId}")
    public ResponseEntity<PipelineStageModel> getStageById(@PathVariable String stageId) {
        return ResponseEntity.ok(stageService.getStageById(stageId));
    }

    @GetMapping
    public ResponseEntity<List<PipelineStageModel>> getAllStages() {
        return ResponseEntity.ok(stageService.getAllStages());
    }
    @PutMapping("/{stageId}")
    public ResponseEntity<PipelineStageModel> updateStage(
            @PathVariable String stageId,
            @RequestBody PipelineStageModel updatedStage) {
        PipelineStageModel stage = stageService.updateStage(stageId, updatedStage);
        return ResponseEntity.ok(stage);
    }


    @DeleteMapping("/{stageId}")
    public ResponseEntity<Void> deleteStage(@PathVariable String stageId) {
        stageService.deleteStage(stageId);
        return ResponseEntity.noContent().build();
    }
}
