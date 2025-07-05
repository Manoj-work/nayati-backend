package com.medhir.rest.repository.accountantModule;

import com.medhir.rest.model.accountantModule.Invoice;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends MongoRepository<Invoice,String> {
    boolean existsByInvoiceNumber(String invoiceNumber);

    Optional<Invoice> findByinvoiceNumber(String invoiceNumber);

    List<Invoice> findAllByProjectId(String projectId);
}
