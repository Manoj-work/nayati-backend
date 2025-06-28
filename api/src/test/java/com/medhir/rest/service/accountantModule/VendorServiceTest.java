package com.medhir.rest.service.accountantModule;

import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.model.accountantModule.VendorModel;
import com.medhir.rest.repository.accountantModule.VendorRepository;
import com.medhir.rest.service.CompanyService;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class VendorServiceTest {

    @Mock
    VendorRepository vendorRepository;

    @Mock
    CompanyService companyService;

    @Mock
    SnowflakeIdGenerator idGenerator;

    @InjectMocks
    VendorService vendorService;

    VendorModel sampleVendor;

    @BeforeEach
    void init() {
        sampleVendor = new VendorModel();
        sampleVendor.setVendorId("VID123");
        sampleVendor.setCompanyId("CID001");
        sampleVendor.setVendorName("ABC Ltd");
    }

    @Test
    void testGetVendorById_whenFound() {
        when(vendorRepository.findByVendorId("VID123")).thenReturn(Optional.of(sampleVendor));
        VendorModel result = vendorService.getVendorById("VID123");
        assertNotNull(result);
        assertEquals("VID123", result.getVendorId());
    }

    @Test
    void testGetVendorById_whenNotFound() {
        when(vendorRepository.findByVendorId("VID999")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> vendorService.getVendorById("VID999"));
    }
}