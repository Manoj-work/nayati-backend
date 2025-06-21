package com.medhir.Attendance.repository;

import com.medhir.Attendance.model.RegisteredUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegisteredUserRepository extends MongoRepository<RegisteredUser, String> {
    List<RegisteredUser> findAll();
    Optional<RegisteredUser> findByEmpId(String empId);
} 