package com.medhir.rest.sales;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PipelineStageRepository extends MongoRepository<PipelineStageModel, String> {
    // Find by custom stageId field
    Optional<PipelineStageModel> findByStageId(String stageId);

    // Case-insensitive name check
    Optional<PipelineStageModel> findByStageNameIgnoreCase(String stageName);

    boolean existsByStageId(String stageId);
    List<PipelineStageModel> findAllByOrderByOrderAsc();

    // Custom delete by stageId
    void deleteByStageId(String stageId);
}
