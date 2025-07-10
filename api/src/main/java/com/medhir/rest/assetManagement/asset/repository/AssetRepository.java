package com.medhir.rest.assetManagement.asset.repository;

import com.medhir.rest.assetManagement.asset.model.Asset;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AssetRepository extends MongoRepository<Asset, String> {
    Optional<Asset> findByAssetId(String assetId);
} 