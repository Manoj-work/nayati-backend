package com.medhir.rest.sales.repository;

import com.medhir.rest.sales.model.PipelineStage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PipelineStageRepository extends MongoRepository<PipelineStage, String> {
    
    // Find all active stages ordered by orderIndex
    List<PipelineStage> findByIsActiveTrueOrderByOrderIndexAsc();
    
    // Find stage by name (case insensitive)
    Optional<PipelineStage> findByNameIgnoreCase(String name);
    
    // Check if stage name exists (case insensitive)
    boolean existsByNameIgnoreCase(String name);
    
    // Find stages by active status
    List<PipelineStage> findByIsActive(boolean isActive);
    
    // Find stage with highest order index
    Optional<PipelineStage> findTopByOrderByOrderIndexDesc();
    
    // Find stages with order index greater than or equal to given value
    List<PipelineStage> findByOrderIndexGreaterThanEqual(int orderIndex);
    
    // Find by custom Snowflake stageId
    Optional<PipelineStage> findByStageId(String stageId);
    
    // Add existence check by Snowflake stageId
    boolean existsByStageId(String stageId);
} 