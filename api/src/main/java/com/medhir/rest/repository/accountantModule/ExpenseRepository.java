package com.medhir.rest.repository.accountantModule;

import com.medhir.rest.model.accountantModule.Expense;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpenseRepository extends MongoRepository<Expense, String> {
    Optional<Expense> findByExpenseId(String expenseId);
    boolean existsByExpenseId(String expenseId);
    void deleteByExpenseId(String expenseId);
}
