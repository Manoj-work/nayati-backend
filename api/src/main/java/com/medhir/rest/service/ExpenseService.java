package com.medhir.rest.service;

import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.model.Expense;
import com.medhir.rest.repository.CompanyRepository;
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
    private CompanyRepository companyRepository;

    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Autowired
    private MinioService minioService;

    public Expense createExpense(Expense expense, MultipartFile receiptInvoiceAttachment, MultipartFile paymentProof) {

        boolean exists = companyRepository.existsByCompanyId(expense.getCompanyId());
        if (!exists) {
            throw new ResourceNotFoundException("Company not found with CompanyId: " + expense.getCompanyId());
        }

        expense.setExpenseId("EXP-" + snowflakeIdGenerator.nextId());

        if (receiptInvoiceAttachment == null || receiptInvoiceAttachment.isEmpty()) {
            throw new IllegalArgumentException("Receipt/Invoice file is required");
        }

        try {
            String fileUrl = minioService.UploadexpensesImg(receiptInvoiceAttachment, expense.getProjectId());
            expense.setReceiptInvoiceAttachmentUrl(fileUrl);

            if (paymentProof != null && !paymentProof.isEmpty()) {
                String paymentProofUrl = minioService.UploadexpensesImg(paymentProof, expense.getProjectId());
                expense.setPaymentProofUrl(paymentProofUrl);
            }
        } catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }

        return expenseRepository.insert(expense);
    }


    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public Expense updateExpense(String expenseId, Expense updatedExpense, MultipartFile receiptInvoiceAttachment, MultipartFile paymentProof) {
        Expense expense = expenseRepository.findByExpenseId(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with ID: " + expenseId));

        // Set basic fields
        expense.setExpenseType(updatedExpense.getExpenseType());
        expense.setClientName(updatedExpense.getClientName());
        expense.setProjectId(updatedExpense.getProjectId());
        expense.setExpenseCategory(updatedExpense.getExpenseCategory());
        expense.setVendorName(updatedExpense.getVendorName());
        expense.setTotalExpenseAmount(updatedExpense.getTotalExpenseAmount());
        expense.setReimbursementAmount(updatedExpense.getReimbursementAmount());
        expense.setGstCredit(updatedExpense.getGstCredit());
        expense.setNotesDescription(updatedExpense.getNotesDescription());

        if (updatedExpense.getStatus() != null) {
            expense.setStatus(updatedExpense.getStatus());
        }

        if (updatedExpense.getRejectionComment() != null) {
            expense.setRejectionComment(updatedExpense.getRejectionComment());
        }

        // Upload files if provided
        try {
            if (receiptInvoiceAttachment != null && !receiptInvoiceAttachment.isEmpty()) {
                String fileUrl = minioService.UploadexpensesImg(receiptInvoiceAttachment, expense.getProjectId());
                expense.setReceiptInvoiceAttachmentUrl(fileUrl);
            }

            if (paymentProof != null && !paymentProof.isEmpty()) {
                String paymentProofUrl = minioService.UploadexpensesImg(paymentProof, expense.getProjectId());
                expense.setPaymentProofUrl(paymentProofUrl);
            }
        } catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }

        return expenseRepository.save(expense);
    }


    public void deleteExpense(String expenseId) {
        if (!expenseRepository.existsByExpenseId(expenseId)) {
            throw new ResourceNotFoundException("Expense not found with ID: " + expenseId);
        }
        expenseRepository.deleteByExpenseId(expenseId);
    }

    public Expense getExpenseByExpenseId(String expenseId) {
        return expenseRepository.findByExpenseId(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with ID: " + expenseId));
    }

    public List<Expense> getAllExpensesByCompanyId(String companyId) {
        boolean exists = companyRepository.existsByCompanyId(companyId);
        if (!exists) {
            throw new ResourceNotFoundException("Company not found with CompanyId: " + companyId);
        }
        return expenseRepository.findByCompanyId(companyId);
    }
}
