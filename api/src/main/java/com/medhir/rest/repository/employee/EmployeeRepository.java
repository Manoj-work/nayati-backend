package com.medhir.rest.repository.employee;

import com.medhir.rest.model.employee.EmployeeModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EmployeeRepository extends MongoRepository<EmployeeModel, String> {
    Optional<EmployeeModel> findByEmailPersonal(String email);

    Optional<EmployeeModel> findByPhone(String phone);

    Optional<EmployeeModel> findByEmployeeId(String employeeId);

    List<EmployeeModel> findByUpdateStatus(String updateStatus);

    List<EmployeeModel> findByReportingManager(String reportingManager);

    Optional<EmployeeModel> findByCompanyIdAndEmployeeId(String companyId, String employeeId);

    List<EmployeeModel> findByCompanyId(String companyId);

    List<EmployeeModel> findByCompanyIdAndReportingManager(String companyId, String reportingManager);

    List<EmployeeModel> findByDepartmentAndDesignationIn(String department, List<String> designations);

    List<EmployeeModel> findByCompanyIdAndUpdateStatus(String companyId, String updateStatus);

    List<EmployeeModel> findByReportingManagerAndUpdateStatus(String reportingManager, String updateStatus);

    List<EmployeeModel> findByModuleIdsContaining(String moduleId);

    List<EmployeeModel> findAllByModuleIdsContaining(String moduleId);

    List<EmployeeModel> findByEmployeeIdIn(Set<String> employeeIds);
}
