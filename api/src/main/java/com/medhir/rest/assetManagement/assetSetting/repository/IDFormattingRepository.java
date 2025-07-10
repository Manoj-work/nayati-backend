package com.medhir.rest.assetManagement.assetSetting.repository;

import com.medhir.rest.assetManagement.assetSetting.model.IDFormatting;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IDFormattingRepository extends MongoRepository<IDFormatting, String> {
    Optional<IDFormatting> findByCategoryId(String categoryId);
    Optional<IDFormatting> findByIdFormattingId(String idFormattingId);
    void deleteByCategoryId(String categoryId);
    void deleteByIdFormattingId(String idFormattingId);
} 