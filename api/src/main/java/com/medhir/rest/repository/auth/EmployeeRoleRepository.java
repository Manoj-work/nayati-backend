package com.medhir.rest.repository.auth;

import com.medhir.rest.model.EmployeeModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRoleRepository extends MongoRepository<EmployeeModel, String> {
    Optional<EmployeeModel> findByEmployeeId(String employeeId);
} 