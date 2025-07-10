package com.medhir.rest.assetManagement.assetSetting.repository;

import com.medhir.rest.assetManagement.assetSetting.model.StatusLabel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusLabelRepository extends MongoRepository<StatusLabel, String> {
    Optional<StatusLabel> findByStatusLabelId(String statusLabelId);
    void deleteByStatusLabelId(String statusLabelId);
} 