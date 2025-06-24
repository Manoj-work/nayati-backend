package com.medhir.rest.repository.accountantModule;

import com.medhir.rest.model.accountantModule.PaymentModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<PaymentModel, String> {
    Optional<PaymentModel> findByPaymentId(String paymentId);
} 