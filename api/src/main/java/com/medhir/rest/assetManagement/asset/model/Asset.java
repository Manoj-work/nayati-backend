package com.medhir.rest.assetManagement.asset.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.Map;

/**
 * Represents an Asset in the Asset Management system.
 * Includes core identification, financial, assignment, and custom fields.
 */
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

    public Asset() {}
    public Asset(String id, String assetId, String name, String categoryId, String serialNumber, String locationId, String statusLabelId, String assignedTo, String purchaseDate, Double purchaseCost, Long vendorId, String invoiceNumber, String warrantyExpiry, String invoiceScanUrl, Map<String, Object> customFields, Double gstRate, Boolean inputTaxCreditEligible) {
        this.id = id;
        this.assetId = assetId;
        this.name = name;
        this.categoryId = categoryId;
        this.serialNumber = serialNumber;
        this.locationId = locationId;
        this.statusLabelId = statusLabelId;
        this.assignedTo = assignedTo;
        this.purchaseDate = purchaseDate;
        this.purchaseCost = purchaseCost;
        this.vendorId = vendorId;
        this.invoiceNumber = invoiceNumber;
        this.warrantyExpiry = warrantyExpiry;
        this.invoiceScanUrl = invoiceScanUrl;
        this.customFields = customFields;
        this.gstRate = gstRate;
        this.inputTaxCreditEligible = inputTaxCreditEligible;
    }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAssetId() { return assetId; }
    public void setAssetId(String assetId) { this.assetId = assetId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    public String getLocationId() { return locationId; }
    public void setLocationId(String locationId) { this.locationId = locationId; }
    public String getStatusLabelId() { return statusLabelId; }
    public void setStatusLabelId(String statusLabelId) { this.statusLabelId = statusLabelId; }
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public String getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(String purchaseDate) { this.purchaseDate = purchaseDate; }
    public Double getPurchaseCost() { return purchaseCost; }
    public void setPurchaseCost(Double purchaseCost) { this.purchaseCost = purchaseCost; }
    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public String getWarrantyExpiry() { return warrantyExpiry; }
    public void setWarrantyExpiry(String warrantyExpiry) { this.warrantyExpiry = warrantyExpiry; }
    public String getInvoiceScanUrl() { return invoiceScanUrl; }
    public void setInvoiceScanUrl(String invoiceScanUrl) { this.invoiceScanUrl = invoiceScanUrl; }
    public Map<String, Object> getCustomFields() { return customFields; }
    public void setCustomFields(Map<String, Object> customFields) { this.customFields = customFields; }
    public Double getGstRate() { return gstRate; }
    public void setGstRate(Double gstRate) { this.gstRate = gstRate; }
    public Boolean getInputTaxCreditEligible() { return inputTaxCreditEligible; }
    public void setInputTaxCreditEligible(Boolean inputTaxCreditEligible) { this.inputTaxCreditEligible = inputTaxCreditEligible; }
} 