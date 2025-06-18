package com.medhir.rest.service;

import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.model.Expense;
import com.medhir.rest.repository.ExpenseRepository;
import com.medhir.rest.utils.MinioService;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Autowired
    private MinioService minioService;

    public Expense createExpense(Expense expense, MultipartFile receiptInvoiceAttachment, MultipartFile paymentProof) {

        expense.setExpenseId("EXP-" + snowflakeIdGenerator.nextId());
        
        // Upload receipt/invoice file to MinIO and get URL
        String fileUrl = minioService.UploadexpensesImg(receiptInvoiceAttachment, expense.getProjectId());
        expense.setReceiptInvoiceAttachmentUrl(fileUrl);
        
        // Upload payment proof file to MinIO if provided
        if (paymentProof != null && !paymentProof.isEmpty()) {
            String paymentProofUrl = minioService.UploadexpensesImg(paymentProof, expense.getProjectId());
            expense.setPaymentProof(paymentProofUrl);
        }
        
        return expenseRepository.insert(expense);
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public Expense updateExpense(String expenseId, Expense updatedExpense, MultipartFile receiptInvoiceAttachment, MultipartFile paymentProof) {
        Optional<Expense> existing = expenseRepository.findByExpenseId(expenseId);
        if (existing.isEmpty()) {
            throw new ResourceNotFoundException("Expense not found with ID: " + expenseId);
        }

        Expense expense = existing.get();
        expense.setExpenseType(updatedExpense.getExpenseType());
        expense.setClientName(updatedExpense.getClientName());
        expense.setProjectId(updatedExpense.getProjectId());
        expense.setExpenseCategory(updatedExpense.getExpenseCategory());
        expense.setVendorName(updatedExpense.getVendorName());
        expense.setTotalExpenseAmount(updatedExpense.getTotalExpenseAmount());
        expense.setReimbursementAmount(updatedExpense.getReimbursementAmount());
        expense.setGstCredit(updatedExpense.getGstCredit());
        expense.setNotesDescription(updatedExpense.getNotesDescription());
        expense.setStatus(updatedExpense.getStatus());
        expense.setRejectionComment(updatedExpense.getRejectionComment());
        
        // If a new receipt/invoice file is provided, upload it to MinIO and update the URL
        if (receiptInvoiceAttachment != null && !receiptInvoiceAttachment.isEmpty()) {
            String fileUrl = minioService.UploadexpensesImg(receiptInvoiceAttachment, expense.getProjectId());
            expense.setReceiptInvoiceAttachmentUrl(fileUrl);
        }
        
        // If a new payment proof file is provided, upload it to MinIO and update the URL
        if (paymentProof != null && !paymentProof.isEmpty()) {
            String paymentProofUrl = minioService.UploadexpensesImg(paymentProof, expense.getProjectId());
            expense.setPaymentProof(paymentProofUrl);
        }
        
        return expenseRepository.save(expense);
    }

    public void deleteExpense(String expenseId) {
        if (!expenseRepository.existsByExpenseId(expenseId)) {
            throw new ResourceNotFoundException("Expense not found with ID: " + expenseId);
        }
        expenseRepository.deleteByExpenseId(expenseId);
    }

    public Optional<Expense> getExpenseByExpenseId(String expenseId) {
        return Optional.ofNullable(expenseRepository.findByExpenseId(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with ID: " + expenseId)));
    }
}
