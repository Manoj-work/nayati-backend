package com.medhir.rest.sales;

import com.medhir.rest.sales.ModelLead;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeadRepository extends MongoRepository<ModelLead, String> {

    Optional<ModelLead> findByEmail(String email);



    Optional<ModelLead> findByLeadId(String leadId);

//    List<ModelLead> findByStatusIgnoreCase(String status);

    List<ModelLead> findByStageId(String stageId);

    List<ModelLead> findByAssignedSalesPerson(String employeeId);
    List<ModelLead> findByAssignedDesigner(String employeeId);

    boolean existsByStageIdIn(List<String> stageIds);
//    List<ModelLead> findByManagerId(String managerId);
//    boolean existsByManagerId(String managerId);
    List<ModelLead> findByAssignedSalesPersonOrAssignedDesigner(String salesPersonId, String designerId);
    List<ModelLead> findByAssignedSalesPersonOrAssignedDesignerOrCreatedBy(String assignedSalesPerson, String assignedDesigner, String createdBy);

    List<ModelLead> findByEmployeeId(String employeeId);
    boolean existsByStageId(String stageId);
    List<ModelLead> findByCreatedByOrAssignedSalesPersonOrAssignedDesigner(String createdBy, String salesPerson, String designer);
    ModelLead save(ModelLead lead);

    List<ModelLead> findAll();

}
