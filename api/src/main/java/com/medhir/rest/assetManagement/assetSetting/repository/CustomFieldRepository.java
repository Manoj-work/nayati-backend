package com.medhir.rest.assetManagement.assetSetting.repository;

import com.medhir.rest.assetManagement.assetSetting.model.CustomField;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomFieldRepository extends MongoRepository<CustomField, String> {
    List<CustomField> findByCategoryId(String categoryId);
    Optional<CustomField> findByCategoryIdAndLabel(String categoryId, String label);
    void deleteByCategoryId(String categoryId);
} 