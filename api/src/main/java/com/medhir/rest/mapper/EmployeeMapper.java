package com.medhir.rest.mapper;

import com.medhir.rest.dto.EmployeeDTO;
import com.medhir.rest.dto.EmployeeWithLeaveDetailsDTO;
import com.medhir.rest.model.EmployeeModel;

public class EmployeeMapper {
    public static EmployeeModel toModel(EmployeeDTO dto) {
        if (dto == null) return null;
        EmployeeModel model = new EmployeeModel();
        model.setCompanyId(dto.getCompanyId());
        model.setName(dto.getName());
        model.setFirstName(dto.getFirstName());
        model.setMiddleName(dto.getMiddleName());
        model.setLastName(dto.getLastName());
        model.setPhone(dto.getPhone());
        model.setAlternatePhone(dto.getAlternatePhone());
        model.setRoles(dto.getRoles());
        model.setModuleIds(dto.getModuleIds());
        model.setEmailPersonal(dto.getEmailPersonal());
        model.setEmailOfficial(dto.getEmailOfficial());
        model.setDesignation(dto.getDesignation());
        model.setFathersName(dto.getFathersName());
        model.setOvertimeEligibile(dto.isOvertimeEligibile());
        model.setPfEnrolled(dto.isPfEnrolled());
        model.setUanNumber(dto.getUanNumber());
        model.setEsicEnrolled(dto.isEsicEnrolled());
        model.setEsicNumber(dto.getEsicNumber());
        model.setWeeklyOffs(dto.getWeeklyOffs());
        model.setEmployeeImgUrl(dto.getEmployeeImgUrl());
        model.setJoiningDate(dto.getJoiningDate());
        model.setDepartment(dto.getDepartment());
        model.setGender(dto.getGender());
        model.setReportingManager(dto.getReportingManager());
        model.setPermanentAddress(dto.getPermanentAddress());
        model.setCurrentAddress(dto.getCurrentAddress());
        model.setLeavePolicyId(dto.getLeavePolicyId());
        model.setUpdateStatus(dto.getUpdateStatus());
        model.setAssignTo(dto.getAssignTo());
        // Map nested IdProofs
        if (dto.getIdProofs() != null) {
            EmployeeModel.IdProofs idProofs = new EmployeeModel.IdProofs();
            idProofs.setAadharNo(dto.getIdProofs().getAadharNo());
            idProofs.setAadharImgUrl(dto.getIdProofs().getAadharImgUrl());
            idProofs.setPanNo(dto.getIdProofs().getPanNo());
            idProofs.setPancardImgUrl(dto.getIdProofs().getPancardImgUrl());
            idProofs.setPassport(dto.getIdProofs().getPassport());
            idProofs.setPassportImgUrl(dto.getIdProofs().getPassportImgUrl());
            idProofs.setDrivingLicense(dto.getIdProofs().getDrivingLicense());
            idProofs.setDrivingLicenseImgUrl(dto.getIdProofs().getDrivingLicenseImgUrl());
            idProofs.setVoterId(dto.getIdProofs().getVoterId());
            idProofs.setVoterIdImgUrl(dto.getIdProofs().getVoterIdImgUrl());
            model.setIdProofs(idProofs);
        }
        // Map nested BankDetails
        if (dto.getBankDetails() != null) {
            EmployeeModel.BankDetails bankDetails = new EmployeeModel.BankDetails();
            bankDetails.setAccountNumber(dto.getBankDetails().getAccountNumber());
            bankDetails.setAccountHolderName(dto.getBankDetails().getAccountHolderName());
            bankDetails.setIfscCode(dto.getBankDetails().getIfscCode());
            bankDetails.setBankName(dto.getBankDetails().getBankName());
            bankDetails.setBranchName(dto.getBankDetails().getBranchName());
            bankDetails.setUpiId(dto.getBankDetails().getUpiId());
            bankDetails.setUpiPhoneNumber(dto.getBankDetails().getUpiPhoneNumber());
            bankDetails.setPassbookImgUrl(dto.getBankDetails().getPassbookImgUrl());
            model.setBankDetails(bankDetails);
        }
        // Map nested SalaryDetails
        if (dto.getSalaryDetails() != null) {
            EmployeeModel.SalaryDetails salaryDetails = new EmployeeModel.SalaryDetails();
            salaryDetails.setAnnualCtc(dto.getSalaryDetails().getAnnualCtc());
            salaryDetails.setMonthlyCtc(dto.getSalaryDetails().getMonthlyCtc());
            salaryDetails.setBasicSalary(dto.getSalaryDetails().getBasicSalary());
            salaryDetails.setHra(dto.getSalaryDetails().getHra());
            salaryDetails.setAllowances(dto.getSalaryDetails().getAllowances());
            salaryDetails.setEmployerPfContribution(dto.getSalaryDetails().getEmployerPfContribution());
            salaryDetails.setEmployeePfContribution(dto.getSalaryDetails().getEmployeePfContribution());
            model.setSalaryDetails(salaryDetails);
        }
        return model;
    }

    public static EmployeeDTO toDTO(EmployeeModel model) {
        if (model == null) return null;
        EmployeeDTO dto = new EmployeeDTO();
        dto.setCompanyId(model.getCompanyId());
        dto.setName(model.getName());
        dto.setFirstName(model.getFirstName());
        dto.setMiddleName(model.getMiddleName());
        dto.setLastName(model.getLastName());
        dto.setPhone(model.getPhone());
        dto.setAlternatePhone(model.getAlternatePhone());
        dto.setRoles(model.getRoles());
        dto.setModuleIds(model.getModuleIds());
        dto.setEmailPersonal(model.getEmailPersonal());
        dto.setEmailOfficial(model.getEmailOfficial());
        dto.setDesignation(model.getDesignation());
        dto.setFathersName(model.getFathersName());
        dto.setOvertimeEligibile(model.isOvertimeEligibile());
        dto.setPfEnrolled(model.isPfEnrolled());
        dto.setUanNumber(model.getUanNumber());
        dto.setEsicEnrolled(model.isEsicEnrolled());
        dto.setEsicNumber(model.getEsicNumber());
        dto.setWeeklyOffs(model.getWeeklyOffs());
        dto.setEmployeeImgUrl(model.getEmployeeImgUrl());
        dto.setJoiningDate(model.getJoiningDate());
        dto.setDepartment(model.getDepartment());
        dto.setGender(model.getGender());
        dto.setReportingManager(model.getReportingManager());
        dto.setPermanentAddress(model.getPermanentAddress());
        dto.setCurrentAddress(model.getCurrentAddress());
        dto.setLeavePolicyId(model.getLeavePolicyId());
        dto.setUpdateStatus(model.getUpdateStatus());
        dto.setAssignTo(model.getAssignTo());
        // Map nested IdProofs
        if (model.getIdProofs() != null) {
            EmployeeDTO.IdProofsDTO idProofs = new EmployeeDTO.IdProofsDTO();
            idProofs.setAadharNo(model.getIdProofs().getAadharNo());
            idProofs.setAadharImgUrl(model.getIdProofs().getAadharImgUrl());
            idProofs.setPanNo(model.getIdProofs().getPanNo());
            idProofs.setPancardImgUrl(model.getIdProofs().getPancardImgUrl());
            idProofs.setPassport(model.getIdProofs().getPassport());
            idProofs.setPassportImgUrl(model.getIdProofs().getPassportImgUrl());
            idProofs.setDrivingLicense(model.getIdProofs().getDrivingLicense());
            idProofs.setDrivingLicenseImgUrl(model.getIdProofs().getDrivingLicenseImgUrl());
            idProofs.setVoterId(model.getIdProofs().getVoterId());
            idProofs.setVoterIdImgUrl(model.getIdProofs().getVoterIdImgUrl());
            dto.setIdProofs(idProofs);
        }
        // Map nested BankDetails
        if (model.getBankDetails() != null) {
            EmployeeDTO.BankDetailsDTO bankDetails = new EmployeeDTO.BankDetailsDTO();
            bankDetails.setAccountNumber(model.getBankDetails().getAccountNumber());
            bankDetails.setAccountHolderName(model.getBankDetails().getAccountHolderName());
            bankDetails.setIfscCode(model.getBankDetails().getIfscCode());
            bankDetails.setBankName(model.getBankDetails().getBankName());
            bankDetails.setBranchName(model.getBankDetails().getBranchName());
            bankDetails.setUpiId(model.getBankDetails().getUpiId());
            bankDetails.setUpiPhoneNumber(model.getBankDetails().getUpiPhoneNumber());
            bankDetails.setPassbookImgUrl(model.getBankDetails().getPassbookImgUrl());
            dto.setBankDetails(bankDetails);
        }
        // Map nested SalaryDetails
        if (model.getSalaryDetails() != null) {
            EmployeeDTO.SalaryDetailsDTO salaryDetails = new EmployeeDTO.SalaryDetailsDTO();
            salaryDetails.setAnnualCtc(model.getSalaryDetails().getAnnualCtc());
            salaryDetails.setMonthlyCtc(model.getSalaryDetails().getMonthlyCtc());
            salaryDetails.setBasicSalary(model.getSalaryDetails().getBasicSalary());
            salaryDetails.setHra(model.getSalaryDetails().getHra());
            salaryDetails.setAllowances(model.getSalaryDetails().getAllowances());
            salaryDetails.setEmployerPfContribution(model.getSalaryDetails().getEmployerPfContribution());
            salaryDetails.setEmployeePfContribution(model.getSalaryDetails().getEmployeePfContribution());
            dto.setSalaryDetails(salaryDetails);
        }
        return dto;
    }

    public static EmployeeWithLeaveDetailsDTO toEmployeeWithLeaveDetailsDTO(EmployeeModel model) {
        if (model == null) return null;
        EmployeeWithLeaveDetailsDTO dto = new EmployeeWithLeaveDetailsDTO();
        // Copy all fields from EmployeeModel to EmployeeWithLeaveDetailsDTO
        dto.setCompanyId(model.getCompanyId());
        dto.setName(model.getName());
        dto.setFirstName(model.getFirstName());
        dto.setMiddleName(model.getMiddleName());
        dto.setLastName(model.getLastName());
        dto.setPhone(model.getPhone());
        dto.setAlternatePhone(model.getAlternatePhone());
        dto.setRoles(model.getRoles());
        dto.setModuleIds(model.getModuleIds());
        dto.setEmailPersonal(model.getEmailPersonal());
        dto.setEmailOfficial(model.getEmailOfficial());
        dto.setDesignation(model.getDesignation());
        dto.setFathersName(model.getFathersName());
        dto.setOvertimeEligibile(model.isOvertimeEligibile());
        dto.setPfEnrolled(model.isPfEnrolled());
        dto.setUanNumber(model.getUanNumber());
        dto.setEsicEnrolled(model.isEsicEnrolled());
        dto.setEsicNumber(model.getEsicNumber());
        dto.setWeeklyOffs(model.getWeeklyOffs());
        dto.setEmployeeImgUrl(model.getEmployeeImgUrl());
        dto.setJoiningDate(model.getJoiningDate());
        dto.setDepartment(model.getDepartment());
        dto.setGender(model.getGender());
        dto.setReportingManager(model.getReportingManager());
        dto.setPermanentAddress(model.getPermanentAddress());
        dto.setCurrentAddress(model.getCurrentAddress());
        dto.setLeavePolicyId(model.getLeavePolicyId());
        dto.setUpdateStatus(model.getUpdateStatus());
        dto.setAssignTo(model.getAssignTo());
        dto.setIdProofs(model.getIdProofs());
        dto.setBankDetails(model.getBankDetails());
        dto.setSalaryDetails(model.getSalaryDetails());
        // Set employeeId if present
        try {
            java.lang.reflect.Field field = EmployeeModel.class.getDeclaredField("employeeId");
            field.setAccessible(true);
            dto.setEmployeeId((String) field.get(model));
        } catch (Exception ignored) {}
        return dto;
    }

    public static EmployeeModel.SalaryDetails toModel(EmployeeDTO.SalaryDetailsDTO dto) {
        if (dto == null) return null;
        EmployeeModel.SalaryDetails model = new EmployeeModel.SalaryDetails();
        model.setAnnualCtc(dto.getAnnualCtc());
        model.setMonthlyCtc(dto.getMonthlyCtc());
        model.setBasicSalary(dto.getBasicSalary());
        model.setHra(dto.getHra());
        model.setAllowances(dto.getAllowances());
        model.setEmployerPfContribution(dto.getEmployerPfContribution());
        model.setEmployeePfContribution(dto.getEmployeePfContribution());
        return model;
    }
    public static EmployeeModel.BankDetails toModel(EmployeeDTO.BankDetailsDTO dto) {
        if (dto == null) return null;
        EmployeeModel.BankDetails model = new EmployeeModel.BankDetails();
        model.setAccountNumber(dto.getAccountNumber());
        model.setAccountHolderName(dto.getAccountHolderName());
        model.setIfscCode(dto.getIfscCode());
        model.setBankName(dto.getBankName());
        model.setBranchName(dto.getBranchName());
        model.setUpiId(dto.getUpiId());
        model.setUpiPhoneNumber(dto.getUpiPhoneNumber());
        model.setPassbookImgUrl(dto.getPassbookImgUrl());
        return model;
    }
    public static EmployeeModel.IdProofs toModel(EmployeeDTO.IdProofsDTO dto) {
        if (dto == null) return null;
        EmployeeModel.IdProofs model = new EmployeeModel.IdProofs();
        model.setAadharNo(dto.getAadharNo());
        model.setAadharImgUrl(dto.getAadharImgUrl());
        model.setPanNo(dto.getPanNo());
        model.setPancardImgUrl(dto.getPancardImgUrl());
        model.setPassport(dto.getPassport());
        model.setPassportImgUrl(dto.getPassportImgUrl());
        model.setDrivingLicense(dto.getDrivingLicense());
        model.setDrivingLicenseImgUrl(dto.getDrivingLicenseImgUrl());
        model.setVoterId(dto.getVoterId());
        model.setVoterIdImgUrl(dto.getVoterIdImgUrl());
        return model;
    }

    // Alias for toModel for compatibility with service usage
    public static EmployeeModel toEmployeeModel(EmployeeDTO dto) {
        return toModel(dto);
    }
} 