package com.medhir.rest.service.company;

import com.medhir.rest.dto.company.CreateCompanyWithHeadRequest;
import com.medhir.rest.model.CompanyModel;
import com.medhir.rest.model.EmployeeModel;
import com.medhir.rest.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyOrchestratorService {

    private final CompanyService companyService;
    private final EmployeeService employeeService;

    @Transactional
    public CompanyModel createCompanyWithHead(CreateCompanyWithHeadRequest request) {
        // Extract
        var companyDto = request.getCompany();
        var headDto = request.getCompanyHead();

        // Create company
        CompanyModel company = new CompanyModel();
        company.setName(companyDto.getName());
        company.setEmail(companyDto.getEmail());
        company.setPhone(companyDto.getPhone());
        company.setGst(companyDto.getGst());
        company.setRegAdd(companyDto.getRegAdd());
        company.setPrefixForEmpID(companyDto.getPrefixForEmpID());
        company = companyService.createCompany(company);

        // Create head
        EmployeeModel headEmployee = employeeService.createCompanyHeadEmployee(
                company.getCompanyId(),
                headDto.getFirstName(),
                headDto.getMiddleName(),
                headDto.getLastName(),
                headDto.getEmail(),
                headDto.getPhone()
        );

        company.setCompanyHeadId(headEmployee.getEmployeeId());
        company = companyService.updateCompany(company.getCompanyId(), company);

        return company;
    }

}
