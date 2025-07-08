package com.medhir.rest.testModuleforsales;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "customers")
public class Customer {

    @Id
    private String id;

    @Indexed
    private String customerId;

    private String customerName;
    private String email;
    private String contactNumber;
    private String address;
}
