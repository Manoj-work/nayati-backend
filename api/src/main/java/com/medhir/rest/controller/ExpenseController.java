package com.medhir.rest.controller;

import com.medhir.rest.model.Expense;
import com.medhir.rest.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/expenses")
//@Tag(name = "Expense API", description = "Endpoints related to expense operations")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> createExpense(
        @Valid @ModelAttribute Expense expense,
        @RequestParam MultipartFile receiptInvoiceAttachment,
        @RequestParam(required = false) MultipartFile paymentProof) {

         Expense savedExpense = expenseService.createExpense(expense, receiptInvoiceAttachment, paymentProof);

        return ResponseEntity.ok(Map.of(
                    "message", "Expense created successfully!",
                    "expense", savedExpense
        ));
    }


    @GetMapping
    public List<Expense> getAllExpenses() {
        return expenseService.getAllExpenses();
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<Expense>> getExpenseByCompanyId(@PathVariable String companyId){
    List<Expense> expenseList = expenseService.getAllExpensesByCompanyId(companyId);
    return ResponseEntity.ok(expenseList);
    }
    
    @GetMapping("/{expenseId}")
    public ResponseEntity<Expense> getExpenseByExpenseId(@PathVariable String expenseId) {
        Expense expense = expenseService.getExpenseByExpenseId(expenseId);
        return ResponseEntity.ok(expense);
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<Map<String, Object>> updateExpense(
            @PathVariable String expenseId,
            @Valid @ModelAttribute Expense expense,
            @RequestParam(required = false) MultipartFile receiptInvoiceAttachment,
            @RequestParam(required = false) MultipartFile paymentProof) {

        expenseService.updateExpense(expenseId, expense, receiptInvoiceAttachment, paymentProof);

        return ResponseEntity.ok(Map.of("message", "Expense updated successfully!"));
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Map<String, String>> deleteExpense(@PathVariable String expenseId) {
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.ok(Map.of("message", "Expense deleted successfully!"));
    }
}
