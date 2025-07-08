package com.medhir.rest.controller.accountantModule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medhir.rest.dto.accountingModule.expense.CreateExpenseRequest;
import com.medhir.rest.dto.accountingModule.expense.ExpenseResponse;
import com.medhir.rest.dto.accountingModule.expense.UpdateExpenseRequest;
import com.medhir.rest.model.accountantModule.Expense;
import com.medhir.rest.service.accountantModule.ExpenseService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/expenses")
//@Tag(name = "Expense API", description = "Endpoints related to expense operations")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<?> createExpense(
            @Valid @RequestPart("expense") String expenseJson,
            @RequestPart("receiptInvoiceAttachment") MultipartFile receipt,
            @RequestPart(value = "paymentProof", required = false) MultipartFile paymentProof) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        CreateExpenseRequest expense = mapper.readValue(expenseJson,CreateExpenseRequest.class);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication : {}" ,authentication.getAuthorities());
        boolean isAccountant = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equalsIgnoreCase("ACCOUNTANT"));

        Expense savedExpense = expenseService.createExpense(expense, receipt, paymentProof,isAccountant);

        return ResponseEntity.ok(Map.of(
                    "message", "Expense created successfully!"
//                    "expense", savedExpense
        ));
    }

    @GetMapping
    public List<ExpenseResponse> getAllExpenses() {
        return expenseService.getAllExpenses();
    }

    @GetMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponse> getExpenseByExpenseId(@PathVariable String expenseId) {
        ExpenseResponse expense = expenseService.getExpenseById(expenseId);
        return ResponseEntity.ok(expense);
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<?> updateExpense(
            @PathVariable String expenseId,
            @Valid @RequestPart("expense") String expenseJson,
            @RequestPart(value = "receiptInvoiceAttachment", required = false) MultipartFile receipt,
            @RequestPart(value = "paymentProof", required = false) MultipartFile paymentProof
    ) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        UpdateExpenseRequest request = mapper.readValue(expenseJson, UpdateExpenseRequest.class);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAccountant = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equalsIgnoreCase("ACCOUNTANT"));

        Expense updatedExpense = expenseService.updateExpense(expenseId,request, receipt, paymentProof, isAccountant);

        return ResponseEntity.ok(Map.of(
                "message", "Expense updated successfully!"
//            "expense", updatedExpense
        ));
    }

}
