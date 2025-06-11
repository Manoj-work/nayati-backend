package com.medhir.rest.repository;

import com.medhir.rest.model.LeadModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LeadRepository extends MongoRepository<LeadModel, String> {
    Optional<LeadModel> findByName(String name);
    boolean existsByContactNumber(String contactNumber);
    boolean existsByEmail(String email);
    Optional<LeadModel> findByLeadId(String leadId);
    // Custom query methods can be added here if needed
} 