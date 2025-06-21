package com.medhir.rest.controller;

import com.medhir.rest.model.Expense;
import com.medhir.rest.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/expenses")
//@Tag(name = "Expense API", description = "Endpoints related to expense operations")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<Map<String, Object>> createExpense(
//            @RequestParam String expenseType,
//            @RequestParam String clientName,
//            @RequestParam String projectId,
//            @RequestParam String expenseCategory,
//            @RequestParam String vendorName,
//            @RequestParam BigDecimal totalExpenseAmount,
//            @RequestParam BigDecimal reimbursementAmount,
//            @RequestParam String gstCredit,
//            @RequestParam String notesDescription,
//            @RequestParam MultipartFile receiptInvoiceAttachment,
//            @RequestParam(required = false) MultipartFile paymentProof,
//            @RequestParam(required = false) String rejectionComment,
//            @RequestParam(required = false) String status) {
//
//        Expense expense = new Expense();
//        expense.setExpenseType(expenseType);
//        expense.setClientName(clientName);
//        expense.setProjectId(projectId);
//        expense.setExpenseCategory(expenseCategory);
//        expense.setVendorName(vendorName);
//        expense.setTotalExpenseAmount(totalExpenseAmount);
//        expense.setReimbursementAmount(reimbursementAmount);
//        expense.setGstCredit(gstCredit);
//        expense.setNotesDescription(notesDescription);
//        expense.setRejectionComment(rejectionComment);
//        expense.setStatus(status != null ? status : "pending");
//
//        Expense savedExpense = expenseService.createExpense(expense, receiptInvoiceAttachment, paymentProof);
//        return ResponseEntity.ok(Map.of(
//                "message", "Expense created successfully!"
////                "expense", savedExpense
//        ));
//    }

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
