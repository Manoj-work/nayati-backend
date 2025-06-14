package com.medhir.rest.repository.leave;

import com.medhir.rest.model.leave.LeaveModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRepository extends MongoRepository<LeaveModel, String> {
    List<LeaveModel> findByEmployeeId(String employeeId);
    List<LeaveModel> findByStatus(String status);
    List<LeaveModel> findByCompanyIdAndStatus(String companyId, String status);
    List<LeaveModel> findByEmployeeIdAndStartDateAndLeaveType(String employeeId, LocalDate startDate, String leaveType);
    List<LeaveModel> findByEmployeeIdAndStartDateAndStatus(String employeeId, LocalDate startDate, String status);
    Optional<LeaveModel> findFirstByEmployeeIdAndStartDateAndStatusOrderByIdDesc(String employeeId, LocalDate startDate, String status);
    Optional<LeaveModel> findByLeaveId(String leaveId);
    List<LeaveModel> findByEmployeeIdInAndStatus(List<String> employeeIds, String status);
}
