package com.medhir.rest.service;

import com.medhir.rest.model.ReimbursementModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.medhir.rest.repository.ReimbursementRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReimbursementService {

    @Autowired
    private ReimbursementRepository reimbursementRepository;

    public ReimbursementModel createReimbursement(ReimbursementModel reimbursement) {
        reimbursement.setStatus("Pending");
        reimbursement.setCreatedAt(LocalDateTime.now());
        return reimbursementRepository.save(reimbursement);
    }

    public List<ReimbursementModel> getAllReimbursements() {
        List<ReimbursementModel> reimbursements = reimbursementRepository.findAll();
        // Always show status as Pending
        reimbursements.forEach(r -> r.setStatus("Pending"));
        return reimbursements;
    }

    public List<ReimbursementModel> getReimbursementsByEmployeeId(String employeeId) {
        return reimbursementRepository.findByEmployeeId(employeeId);
    }
}
