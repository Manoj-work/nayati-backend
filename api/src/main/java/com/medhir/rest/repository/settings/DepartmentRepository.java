package com.medhir.rest.repository.settings;

import com.medhir.rest.model.settings.DepartmentModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface DepartmentRepository extends MongoRepository<DepartmentModel, String> {
    Optional<DepartmentModel> findByName(String name);
    boolean existsByName(String name);
    Optional<DepartmentModel> findByDepartmentId(String departmentId);
    boolean existsByDepartmentId(String departmentId);
    List<DepartmentModel> findByCompanyId(String companyId);
    boolean existsByNameAndCompanyId(String name, String companyId);
    @Query("{ 'departmentId': { '$in': ?0 } }")
    List<DepartmentModel> findByDepartmentIdIn(Set<String> ids);
} 