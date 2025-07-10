package com.medhir.rest.settings;

import com.medhir.rest.model.settings.DepartmentModel;
import com.medhir.rest.model.settings.DesignationModel;
import com.medhir.rest.repository.settings.DesignationRepository;
import com.medhir.rest.service.settings.DepartmentService;
import com.medhir.rest.service.settings.DesignationService;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DesignationServiceTest {

    @Mock
    DesignationRepository designationRepository;

    @Mock
    SnowflakeIdGenerator snowflakeIdGenerator;

    @Mock
    DepartmentService departmentService;

    @Mock
    MongoTemplate mongoTemplate;

    @InjectMocks
    DesignationService designationService;

    private DesignationModel designation;
   private DepartmentModel department;
    @BeforeEach
    public void setup() {
        DepartmentModel department = new DepartmentModel();
        department.setId(String.valueOf(101L));
        department.setName("Management");

        designation = new DesignationModel();
        designation.setId(String.valueOf(100L));
        designation.setName("Hr manager");
        designation.setDepartment(department.getName());
//        designation.setCreatedAt(LocalDateTime.now());
//        designation.setCreatedBy("TestUser");
    }

//    @Test
//    public void createDesignationSuccessful() {
//        when(designationRepository.existsByNameAndDepartment(
//                designation.getName(), designation.getDepartment())).thenReturn(false);
//
//        when(designationRepository.save(any(DesignationModel.class)))
//                .thenReturn(savedDesignation);
//
//    }
}
