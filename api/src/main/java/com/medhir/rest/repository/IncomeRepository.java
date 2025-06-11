package com.medhir.rest.repository;

import com.medhir.rest.model.IncomeModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.List;

public interface IncomeRepository extends MongoRepository<IncomeModel, String> {
    List<IncomeModel> findBySubmittedBy(String submittedBy);
    Optional<IncomeModel> findByIncomeId(String incomeId);
    List<IncomeModel> findBySubmittedByIn(List<String> submittedByList);
    List<IncomeModel> findByCompanyIdOrderBySubmittedBy(String companyId);
    List<IncomeModel> findByCompanyIdAndStatusOrderBySubmittedBy(String companyId, String status);
    List<IncomeModel> findBySubmittedByInAndStatusOrderBySubmittedBy(List<String> submittedByList, String status);
}