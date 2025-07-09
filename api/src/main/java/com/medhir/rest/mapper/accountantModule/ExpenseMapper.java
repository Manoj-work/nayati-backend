package com.medhir.rest.mapper.accountantModule;

import com.medhir.rest.dto.accountantModule.expense.CreateExpenseRequest;
import com.medhir.rest.dto.accountantModule.expense.UpdateExpenseRequest;
import com.medhir.rest.model.accountantModule.Expense;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    Expense toExpense(CreateExpenseRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateExpenseFromRequest(UpdateExpenseRequest request, @MappingTarget Expense expense);
}
