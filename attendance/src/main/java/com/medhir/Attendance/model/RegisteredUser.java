package com.medhir.Attendance.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "Registered-Users")
public class RegisteredUser {
    @Id
    private String id;
    private String empId;
    private String name;
    private String imgUrl;
} 