package com.medhir.rest.repository.accountantModule;

import com.medhir.rest.model.accountantModule.PurchaseOrderModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderRepository extends MongoRepository<PurchaseOrderModel, String> {
    // Additional query methods if needed
} 