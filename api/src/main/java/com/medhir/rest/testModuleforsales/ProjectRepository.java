package com.medhir.rest.testModuleforsales;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProjectRepository extends MongoRepository<Project, String> {

    Optional<Project> findByProjectId(String projectId);
    List<Project> findAllByProjectIdIn(Set<String> projectIds);
}