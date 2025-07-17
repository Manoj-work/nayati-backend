package com.medhir.rest.mapper.company;

import com.medhir.rest.dto.company.CompanyHeadResponseDTO;
import com.medhir.rest.dto.company.CompanyResponseDTO;
import com.medhir.rest.model.company.CompanyModel;
import com.medhir.rest.model.employee.EmployeeModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    @Mapping(target = "companyHeads", ignore = true)
    CompanyResponseDTO toCompanyResponseDTO(CompanyModel company);

    @Mapping(source = "emailPersonal", target = "email")
    CompanyHeadResponseDTO toCompanyHeadResponseDTO(EmployeeModel head);
}
