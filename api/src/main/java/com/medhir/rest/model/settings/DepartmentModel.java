package com.medhir.rest.model.settings;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.medhir.rest.dto.rbac.SimpleModule;
import com.medhir.rest.model.rbac.ModulePermission;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "departments")
public class DepartmentModel {

    @Id
    @JsonIgnore
    private String id;

    private String departmentId;

    @NotBlank(message = "Department name is required")
    private String name;

    @NotBlank(message = "Company ID is required")
    private String companyId;

    private String description;

    private String departmentHead;

//    @NotBlank(message = "Leave policy name is required")
    private String leavePolicy;

    private String weeklyHolidays;

    private String createdAt;
    private String updatedAt;
    private List<SimpleModule> assignedModules = new ArrayList<>();
//    private List<ModulePermission> assignedModules = new ArrayList<>(); // Which modules, features, sub-features are enabled for this company
}