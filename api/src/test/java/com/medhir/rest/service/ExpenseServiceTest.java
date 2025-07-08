package com.medhir.rest.service;

import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.model.Expense;
import com.medhir.rest.repository.CompanyRepository;
import com.medhir.rest.repository.ExpenseRepository;
import com.medhir.rest.utils.MinioService;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private ExpenseService expenseService;

    private Expense mockExpense;

    @Mock
    private MultipartFile receiptFile;

    @Mock
    private MultipartFile paymentProofFile;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        mockExpense = new Expense();
        mockExpense.setExpenseId("EXP-100");
        mockExpense.setCompanyId("CID100");
        mockExpense.setProjectId("PID100");
        mockExpense.setExpenseType("Travel");
    }

    @Test
    public void testCreateExpense_Success() throws Exception {
        when(companyRepository.existsByCompanyId("CID100")).thenReturn(true);
        when(snowflakeIdGenerator.nextId()).thenReturn(100L);
        when(receiptFile.isEmpty()).thenReturn(false);
        when(minioService.UploadexpensesImg(receiptFile, "PID100")).thenReturn("url1");
        when(expenseRepository.insert(any(Expense.class))).thenReturn(mockExpense);

        Expense result = expenseService.createExpense(mockExpense, receiptFile, null);

        assertNotNull(result);
        assertEquals("EXP-100", result.getExpenseId());
    }

    @Test
    public void testCreateExpense_CompanyNotFound() {
        when(companyRepository.existsByCompanyId("CID100")).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> expenseService.createExpense(mockExpense, receiptFile, null));
    }

    @Test
    public void testCreateExpense_ReceiptMissing() {
        when(companyRepository.existsByCompanyId("CID100")).thenReturn(true);
        when(receiptFile == null || receiptFile.isEmpty()).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> expenseService.createExpense(mockExpense, receiptFile, null));
    }

    @Test
    public void testCreateExpense_FileUploadFail() throws Exception {
        when(companyRepository.existsByCompanyId("CID100")).thenReturn(true);
        when(receiptFile.isEmpty()).thenReturn(false);
        when(minioService.UploadexpensesImg(receiptFile, "PID100")).thenThrow(new RuntimeException("Fail"));

        assertThrows(RuntimeException.class, () -> expenseService.createExpense(mockExpense, receiptFile, null));
    }

    @Test
    public void testGetAllExpenses() {
        when(expenseRepository.findAll()).thenReturn(List.of(mockExpense));

        List<Expense> result = expenseService.getAllExpenses();

        assertEquals(1, result.size());
    }

    @Test
    public void testUpdateExpense_Success() throws Exception {
        Expense updated = new Expense();
        updated.setExpenseType("Food");
        updated.setProjectId("PID100");

        when(expenseRepository.findByExpenseId("EXP-100")).thenReturn(Optional.of(mockExpense));
        when(receiptFile.isEmpty()).thenReturn(false);
        when(minioService.UploadexpensesImg(receiptFile, "PID100")).thenReturn("url1");
        when(expenseRepository.save(any(Expense.class))).thenReturn(mockExpense);

        Expense result = expenseService.updateExpense("EXP-100", updated, receiptFile, null);
        assertNotNull(result);
    }

    @Test
    public void testUpdateExpense_NotFound() {
        when(expenseRepository.findByExpenseId("EXP-100")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> expenseService.updateExpense("EXP-100", mockExpense, null, null));
    }

    @Test
    public void testDeleteExpense_Success() {
        when(expenseRepository.existsByExpenseId("EXP-100")).thenReturn(true);

        expenseService.deleteExpense("EXP-100");

        verify(expenseRepository, times(1)).deleteByExpenseId("EXP-100");
    }

    @Test
    public void testDeleteExpense_NotFound() {
        when(expenseRepository.existsByExpenseId("EXP-100")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> expenseService.deleteExpense("EXP-100"));
    }

    @Test
    public void testGetExpenseByExpenseId_Success() {
        when(expenseRepository.findByExpenseId("EXP-100")).thenReturn(Optional.of(mockExpense));

        Expense result = expenseService.getExpenseByExpenseId("EXP-100");

        assertEquals("EXP-100", result.getExpenseId());
    }

    @Test
    public void testGetExpenseByExpenseId_NotFound() {
        when(expenseRepository.findByExpenseId("EXP-100")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> expenseService.getExpenseByExpenseId("EXP-100"));
    }

    @Test
    public void testGetAllExpensesByCompanyId_Success() {
        when(companyRepository.existsByCompanyId("CID100")).thenReturn(true);
        when(expenseRepository.findByCompanyId("CID100")).thenReturn(List.of(mockExpense));

        List<Expense> result = expenseService.getAllExpensesByCompanyId("CID100");

        assertEquals(1, result.size());
    }

    @Test
    public void testGetAllExpensesByCompanyId_CompanyNotFound() {
        when(companyRepository.existsByCompanyId("CID100")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> expenseService.getAllExpensesByCompanyId("CID100"));
    }
}
