package com.medhir.rest.repository.accountantModule;

import com.medhir.rest.model.accountantModule.Receipt;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ReceiptRepository extends MongoRepository<Receipt,String > {

    Optional<Receipt> findByReceiptNumber(String receiptNumber);
    List<Receipt> findAllByProjectId(String projectId);

}
