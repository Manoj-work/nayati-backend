package com.medhir.rest.repository.settings;

import com.medhir.rest.model.settings.DesignationModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface DesignationRepository extends MongoRepository<DesignationModel, String> {
    Optional<DesignationModel> findByName(String name);
    boolean existsByName(String name);
    Optional<DesignationModel> findByDesignationId(String designationId);
    boolean existsByDesignationId(String designationId);
    List<DesignationModel> findByDepartment(String department);
    boolean existsByNameAndDepartment(String name, String department);
    @Query("{ 'designationId': { '$in': ?0 } }")
    List<DesignationModel> findByDesignationIdIn(Set<String> ids);

}