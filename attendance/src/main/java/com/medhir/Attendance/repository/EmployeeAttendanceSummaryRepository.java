package com.medhir.Attendance.repository;

import com.medhir.Attendance.model.EmployeeAttendanceSummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeAttendanceSummaryRepository extends MongoRepository<EmployeeAttendanceSummary, String> {
    Optional<EmployeeAttendanceSummary> findByEmployeeId(String employeeId);
}

