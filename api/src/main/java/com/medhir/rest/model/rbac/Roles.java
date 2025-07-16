package com.medhir.rest.model.rbac;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "roles")
@Getter
@Setter
@CompoundIndexes({
        @CompoundIndex(name = "unique_role_per_company",def = "{'companyId':1,'roleName': 1}", unique = true )
})
public class Roles {

    @Id
    private String roleId;

    private String companyId; // link to Company

    private String roleName; // e.g. "Manager"

    private List<ModulePermission> permissions; // exactly same structure as your CompanyModel.assignedModules

}