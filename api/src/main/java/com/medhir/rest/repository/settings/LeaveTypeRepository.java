package com.medhir.rest.repository.settings;

import com.medhir.rest.model.settings.LeaveTypeModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface LeaveTypeRepository extends MongoRepository<LeaveTypeModel, String> {
    Optional<LeaveTypeModel> findByLeaveTypeName(String leaveTypeName);
    boolean existsByLeaveTypeName(String leaveTypeName);
    Optional<LeaveTypeModel> findByLeaveTypeId(String leaveTypeId);
    boolean existsByLeaveTypeId(String leaveTypeId);
    List<LeaveTypeModel> findByCompanyId(String companyId);

    @Query("{'leaveTypeId': {'$in':?0}}")
    List<LeaveTypeModel> findByLeaveTypeIdIn(Set<String> ids);
}