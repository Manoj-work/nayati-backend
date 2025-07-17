package com.medhir.rest.service.accountantModule;

import com.medhir.rest.dto.accountantModule.expense.CreateExpenseRequest;
import com.medhir.rest.dto.accountantModule.expense.ExpenseResponse;
import com.medhir.rest.dto.accountantModule.expense.UpdateExpenseRequest;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.mapper.accountantModule.ExpenseMapper;
import com.medhir.rest.model.employee.EmployeeModel;
import com.medhir.rest.model.accountantModule.Expense;
import com.medhir.rest.model.accountantModule.VendorModel;
import com.medhir.rest.repository.EmployeeRepository;
import com.medhir.rest.repository.accountantModule.ExpenseRepository;
import com.medhir.rest.repository.accountantModule.VendorRepository;
import com.medhir.rest.utils.MinioService;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;
    @Autowired
    private MinioService minioService;
    @Autowired
    private ExpenseMapper expenseMapper;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private VendorRepository vendorRepository;

    public Expense createExpense(CreateExpenseRequest request, MultipartFile receiptInvoiceAttachment, MultipartFile paymentProof,boolean isAccountant) {

        Expense expense = expenseMapper.toExpense(request);

        if (receiptInvoiceAttachment == null || receiptInvoiceAttachment.isEmpty()) {
            throw new IllegalArgumentException("Receipt/Invoice file is required");
        }

        try {
            String fileUrl = minioService.UploadexpensesImg(receiptInvoiceAttachment, expense.getProjectId());
            expense.setReceiptInvoiceUrl(fileUrl);

            if (isAccountant) {
                if (paymentProof != null && !paymentProof.isEmpty()) {
                    String paymentProofUrl = minioService.UploadexpensesImg(paymentProof, expense.getProjectId());
                    expense.setPaymentProofUrl(paymentProofUrl);
                    expense.setStatus(Expense.Status.PAID);
                } else {
                    expense.setStatus(Expense.Status.PENDING);
                }
            } else {
                // Enforce: non-managers cannot set payment proof or PAID
                expense.setPaymentProofUrl(null);
                expense.setStatus(Expense.Status.PENDING);
            }

        } catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }
        expense.setExpenseId("EXP"+snowflakeIdGenerator.nextId());

        return expenseRepository.insert(expense);
    }

    public List<ExpenseResponse> getAllExpenses() {
        List<Expense> expenses = expenseRepository.findAll();

        // Collect unique employeeIds and vendorIds
        Set<String> employeeIds = expenses.stream().map(Expense::getCreatedBy).collect(Collectors.toSet());
        Set<String> vendorIds = expenses.stream().map(Expense::getVendorId).collect(Collectors.toSet());

        // Fetch them all in 1 call each
        List<EmployeeModel> employees = employeeRepository.findByEmployeeIdIn(employeeIds);
        List<VendorModel> vendors = vendorRepository.findByVendorIdIn(vendorIds);

        // Map for quick lookup
        Map<String, EmployeeModel> employeeMap = employees.stream()
                .collect(Collectors.toMap(EmployeeModel::getEmployeeId, e -> e));
        Map<String, VendorModel> vendorMap = vendors.stream()
                .collect(Collectors.toMap(VendorModel::getVendorId, v -> v));

        // Build response DTOs
        return expenses.stream().map(expense -> {
            ExpenseResponse.EmployeeInfo empInfo = Optional.ofNullable(employeeMap.get(expense.getCreatedBy()))
                    .map(emp -> new ExpenseResponse.EmployeeInfo(emp.getEmployeeId(), emp.getName()))
                    .orElse(null);

            ExpenseResponse.VendorInfo venInfo = Optional.ofNullable(vendorMap.get(expense.getVendorId()))
                    .map(v -> new ExpenseResponse.VendorInfo(v.getVendorId(), v.getVendorName()))
                    .orElse(null);

            return new ExpenseResponse(
                    expense.getExpenseId(),
                    empInfo,
                    venInfo,
                    expense.getDate(),
                    expense.getExpenseType(),
                    expense.getExpenseCategory(),
                    expense.getProjectId(),
                    expense.getAmount(),
                    expense.getNotesDescription(),
                    expense.getReceiptInvoiceUrl(),
                    expense.getPaymentProofUrl(),
                    expense.getStatus().name()
            );
        }).toList();
    }

    public Expense updateExpense(String expenseId,UpdateExpenseRequest request, MultipartFile receiptInvoiceAttachment, MultipartFile paymentProof, boolean isAccountant) {
        // Fetch by unique expenseId
        Expense expense = expenseRepository.findByExpenseId(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        // Update fields
        expenseMapper.updateExpenseFromRequest(request, expense);

        try {
            // If new receipt uploaded, replace it
            if (receiptInvoiceAttachment != null && !receiptInvoiceAttachment.isEmpty()) {
                String newReceiptUrl = minioService.UploadexpensesImg(receiptInvoiceAttachment, expense.getProjectId());
                expense.setReceiptInvoiceUrl(newReceiptUrl);
            }

            // If user is an accountant, allow updating payment proof and status
            if (isAccountant) {
                if (paymentProof != null && !paymentProof.isEmpty()) {
                    String newPaymentProofUrl = minioService.UploadexpensesImg(paymentProof, expense.getProjectId());
                    expense.setPaymentProofUrl(newPaymentProofUrl);
                    expense.setStatus(Expense.Status.PAID);
                }
            } else {
                // Non-accountants can't touch payment proof or mark as PAID
                expense.setPaymentProofUrl(null);
                expense.setStatus(Expense.Status.PENDING);
            }
        } catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }

        return expenseRepository.save(expense);
    }


    public ExpenseResponse getExpenseById(String expenseId) {
        Expense expense = expenseRepository.findByExpenseId(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with ID: " + expenseId));

        // Fetch related Employee and Vendor
        EmployeeModel employee = employeeRepository.findByEmployeeId(expense.getCreatedBy())
                .orElse(null);

        VendorModel vendor = vendorRepository.findByVendorId(expense.getVendorId())
                .orElse(null);

        ExpenseResponse.EmployeeInfo empInfo = (employee != null)
                ? new ExpenseResponse.EmployeeInfo(employee.getEmployeeId(), employee.getName())
                : null;

        ExpenseResponse.VendorInfo venInfo = (vendor != null)
                ? new ExpenseResponse.VendorInfo(vendor.getVendorId(), vendor.getVendorName())
                : null;

        return new ExpenseResponse(
                expense.getExpenseId(),
                empInfo,
                venInfo,
                expense.getDate(),
                expense.getExpenseType(),
                expense.getExpenseCategory(),
                expense.getProjectId(),
                expense.getAmount(),
                expense.getNotesDescription(),
                expense.getReceiptInvoiceUrl(),
                expense.getPaymentProofUrl(),
                expense.getStatus().name()
        );
    }

}
