package com.medhir.rest.testModuleforsales;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "projects")
public class Project {

    @Id
    private String id;
    @Indexed(unique = true)
    private String projectId;

    private String projectName;
    private String customerId;  // Link to Customer
    private String description;
    private String siteAddress;
}

