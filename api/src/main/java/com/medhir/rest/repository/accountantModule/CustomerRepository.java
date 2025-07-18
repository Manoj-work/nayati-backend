package com.medhir.rest.repository.accountantModule;

import com.medhir.rest.model.accountantModule.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CustomerRepository extends MongoRepository<Customer, String> {
    Optional<Customer> findByCustomerId(String customerId);
    List<Customer> findAllByCustomerIdIn(Set<String> customerIds);
}