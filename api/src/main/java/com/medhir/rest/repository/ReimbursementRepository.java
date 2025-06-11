package com.medhir.rest.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.medhir.rest.model.ReimbursementModel;
import java.util.List;

public interface ReimbursementRepository extends MongoRepository<ReimbursementModel, String> {
    List<ReimbursementModel> findByEmployeeId(String employeeId);
}
