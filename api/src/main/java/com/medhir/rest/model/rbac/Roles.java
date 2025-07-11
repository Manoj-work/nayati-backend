package com.medhir.rest.model.rbac;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "roles")
@Getter
@Setter
public class Roles {

    @Id
    private String roleId;

    private String companyId; // link to Company

    private String name; // e.g. "Manager"

    private List<ModulePermission> permissions; // exactly same structure as your CompanyModel.assignedModules

}
