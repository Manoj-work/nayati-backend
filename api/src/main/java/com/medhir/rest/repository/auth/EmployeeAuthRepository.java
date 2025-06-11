package com.medhir.rest.repository.auth;

import com.medhir.rest.model.auth.EmployeeAuth;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeAuthRepository extends MongoRepository<EmployeeAuth, String> {
    Optional<EmployeeAuth> findByEmployeeId(String employeeId);
    Optional<EmployeeAuth> findByEmail(String email);
} 