package com.medhir.rest.sales;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeadModlRepository extends MongoRepository<LeadModl, String> {
    // You can add custom query methods if needed
}
