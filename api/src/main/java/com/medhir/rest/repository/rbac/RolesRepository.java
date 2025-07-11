package com.medhir.rest.repository.rbac;

import com.medhir.rest.model.rbac.Roles;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RolesRepository extends MongoRepository<Roles,String> {

     List<Roles> findByRoleIdIn(List<String> roleIds);
}
