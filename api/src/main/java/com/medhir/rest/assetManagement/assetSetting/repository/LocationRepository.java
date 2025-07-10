package com.medhir.rest.assetManagement.assetSetting.repository;

import com.medhir.rest.assetManagement.assetSetting.model.Location;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends MongoRepository<Location, String> {
    Optional<Location> findByLocationId(String locationId);
    void deleteByLocationId(String locationId);
} 