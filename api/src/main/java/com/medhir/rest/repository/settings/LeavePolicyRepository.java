package com.medhir.rest.repository.settings;

import com.medhir.rest.model.settings.LeavePolicyModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface LeavePolicyRepository extends MongoRepository<LeavePolicyModel, String> {
    Optional<LeavePolicyModel> findByName(String name);
    boolean existsByName(String name);
    Optional<LeavePolicyModel> findByLeavePolicyId(String leavePolicyId);
    boolean existsByLeavePolicyId(String leavePolicyId);
    List<LeavePolicyModel> findByCompanyId(String companyId);

    @Query("{'leavePolicyId': {'$in':?0}}")
    List<LeavePolicyModel> findByLeavePolicyIdIn(Set<String> ids);
} 