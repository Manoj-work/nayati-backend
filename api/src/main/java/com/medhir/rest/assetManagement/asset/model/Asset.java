package com.medhir.rest.assetManagement.asset.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.Map;

/**
 * Represents an Asset in the Asset Management system.
 * Includes core identification, financial, assignment, and custom fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "assets")
public class Asset {
    @Id
    private String id;
    private String assetId;
    private String name;
    private String categoryId;
    private String serialNumber;
    private String locationId;
    private String statusLabelId;
    private String assignedTo;
    private String purchaseDate;
    private Double purchaseCost;
    private Long vendorId;
    private String invoiceNumber;
    private String warrantyExpiry;
    private String invoiceScanUrl;
    private Map<String, Object> customFields;
    private Double gstRate;
    private Boolean inputTaxCreditEligible;
    private String laptopCompany;
    private String processor;
    private String ram;
    private String memory;
    private String graphicsCard;
    private String condition;
    private String accessories;

    private String assignedToTeam;
    private String assignmentDate;



} 