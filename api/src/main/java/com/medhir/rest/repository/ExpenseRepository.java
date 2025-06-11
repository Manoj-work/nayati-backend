package com.medhir.rest.repository;

import com.medhir.rest.model.ExpenseModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends MongoRepository<ExpenseModel, String> {
    List<ExpenseModel> findBySubmittedBy(String submittedBy);
    Optional<ExpenseModel> findByExpenseId(String expenseId);
    List<ExpenseModel> findBySubmittedByIn(List<String> submittedByList);
    List<ExpenseModel> findByCompanyIdOrderBySubmittedBy(String companyId);
    List<ExpenseModel> findByCompanyIdAndStatusOrderBySubmittedBy(String companyId, String status);
    List<ExpenseModel> findBySubmittedByInAndStatusOrderBySubmittedBy(List<String> submittedByList, String status);
}