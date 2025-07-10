package com.medhir.rest.assetManagement.assetSetting.repository;

import com.medhir.rest.assetManagement.assetSetting.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {
    Optional<Category> findByCategoryId(String categoryId);
    void deleteByCategoryId(String categoryId);
} 