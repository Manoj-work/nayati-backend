package com.medhir.rest.sales.repository;

import com.medhir.rest.sales.model.LeadModel;
import com.medhir.rest.sales.repository.KanbanLeadProjection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeadRepository extends MongoRepository<LeadModel, String> {
    // Find leads by stage ID (changed from status for better data integrity)
    List<LeadModel> findByStageId(String stageId);
    List<LeadModel> findBySalesRep(String salesRep);
    List<LeadModel> findBySalesRepAndStageId(String salesRep, String stageId);
    
    // Count leads by stage ID (for pipeline stage management)
    long countByStageId(String stageId);
    
    // If you have a manager/team field, add:
    // List<LeadModel> findByManagerId(String managerId);

    // Find by custom Snowflake leadId
    Optional<LeadModel> findByLeadId(String leadId);

    List<KanbanLeadProjection> findAllBy(Class<KanbanLeadProjection> type);

    Optional<LeadModel> findByProjectName(String projectName);

}
