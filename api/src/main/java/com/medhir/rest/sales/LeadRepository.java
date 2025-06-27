package com.medhir.rest.sales;

import com.medhir.rest.sales.ModelLead;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeadRepository extends MongoRepository<ModelLead, String> {

    Optional<ModelLead> findByEmail(String email);

    Optional<ModelLead> findByContactNumber(String phoneNumber);

    Optional<ModelLead> findByLeadId(String leadId);

    List<ModelLead> findByStatusIgnoreCase(String status);
}
