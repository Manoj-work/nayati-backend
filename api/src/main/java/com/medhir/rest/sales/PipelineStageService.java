package com.medhir.rest.sales;

import com.medhir.rest.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PipelineStageService {

    @Autowired
    private PipelineStageRepository stageRepository;

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    public PipelineStageModel createStage(PipelineStageModel stage) {
        // Validate stage name uniqueness
        if (stageRepository.findByStageNameIgnoreCase(stage.getStageName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Stage with name '" + stage.getStageName() + "' already exists");
        }

        // Generate custom stage ID
        stage.setStageId("SID" + snowflakeIdGenerator.nextId());
        stage.setTimestamp(LocalDateTime.now());

        // Clear form fields if not required
        if (!stage.isFormRequired()) {
            stage.setFormFields(null);
        }

        Integer maxOrder = stageRepository.findAll()
                .stream()
                .map(PipelineStageModel::getOrder)
                .filter(order -> order != null)  // This avoids null values causing errors
                .max(Integer::compareTo)
                .orElse(0);
                 stage.setOrder(maxOrder + 1);

        return stageRepository.save(stage);
    }

    public PipelineStageModel getStageById(String stageId) {
        return stageRepository.findByStageId(stageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stage not found"));
    }
    public List<PipelineStageModel> getAllStagesOrdered() {
        return stageRepository.findAllByOrderByOrderAsc();
    }



    public List<PipelineStageModel> getAllStages() {
        return stageRepository.findAll();
    }
    public PipelineStageModel updateStage(String stageId, PipelineStageModel updatedStage) {
        // Fetch existing stage
        PipelineStageModel existingStage = stageRepository.findByStageId(stageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stage not found"));

        // If stage name is changed, validate uniqueness
        if (!existingStage.getStageName().equalsIgnoreCase(updatedStage.getStageName())) {
            if (stageRepository.findByStageNameIgnoreCase(updatedStage.getStageName()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Stage with name '" + updatedStage.getStageName() + "' already exists");
            }
        }

        // Update fields
        existingStage.setStageName(updatedStage.getStageName());
        existingStage.setColor(updatedStage.getColor());
        existingStage.setFormRequired(updatedStage.isFormRequired());

        // If formRequired is false, clear form fields
//        if (!updatedStage.isFormRequired()) {
//            existingStage.setFormFields(null);
//        } else {
//            existingStage.setFormFields(updatedStage.getFormFields());
//        }

        // Optionally update order if you want to allow changing the order here
        if (updatedStage.getOrder() != null) {
            existingStage.setOrder(updatedStage.getOrder());
        }

        // Update timestamp to now (optional)
        existingStage.setTimestamp(LocalDateTime.now());

        // Save updated stage
        return stageRepository.save(existingStage);
    }


    public void deleteStage(String stageId) {
        // Check if any lead exists with this stageId
        if (leadRepository.existsByStageId(stageId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot delete stage. One or more leads exist in this stage.");
        }
        stageRepository.deleteByStageId(stageId);
    }

}
