package com.medhir.rest.model.accountantModule;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Document(collection = "purchase_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseOrderModel {
    @Id
    @JsonIgnore
    private String id;

    @Indexed(unique = true)
    private String purchaseOrderId;

    @NotBlank(message = "Purchase Order Number is required")
    private String purchaseOrderNumber;

    @Builder.Default
    private String currency = "INR";

    // Company Info
    @NotBlank(message = "Company ID is required")
    private String companyId;

    @NotBlank(message = "Company Address is required")
    private String companyAddress;

    @NotBlank(message = "Vendor ID is required")
    private String vendorId;

    // Vendor Details
    @NotBlank(message = "GSTIN is required")
    private String gstin;

    @NotBlank(message = "Vendor Address is required")
    private String vendorAddress;

    private Double tdsPercentage;

    @NotBlank(message = "Purchase Order Date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String purchaseOrderDate;

    @NotBlank(message = "Purchase Order Delivery Date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String purchaseOrderDeliveryDate;

    @Builder.Default
    private Status status = Status.DRAFT;

    @Valid
    @NotEmpty(message = "At least one purchase order line item is required")
    private List<PurchaseOrderLineItem> purchaseOrderLineItems;

    private List<String> attachmentUrls;

    // Line Items Inner Class with Validation
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PurchaseOrderLineItem {
        @NotBlank(message = "Item name is required")
        private String itemName;

        private String description;

        @NotBlank(message = "HSN or SAC code is required")
        private String hsnOrSac;

        @Min(value = 1, message = "Quantity must be at least 1")
        private int quantity;

        @NotBlank(message = "Unit of Measure is required")
        private String uom;

        @NotNull(message = "Rate is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Rate must be greater than 0")
        private BigDecimal rate;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Amount cannot be negative")
        private BigDecimal amount;

        @NotNull(message = "GST percent is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "GST percent cannot be negative")
        private BigDecimal gstPercent;

        @NotNull(message = "GST amount is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "GST amount cannot be negative")
        private BigDecimal gstAmount;

        @NotNull(message = "Total amount is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Total amount cannot be negative")
        private BigDecimal totalAmount;
    }



    @NotNull(message = "Total before GST is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Total before GST cannot be negative")
    private BigDecimal totalBeforeGST;

    @NotNull(message = "Total GST is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Total GST cannot be negative")
    private BigDecimal totalGST;

    private BigDecimal tdsApplied;


    @NotNull(message = "Final amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Final amount cannot be negative")
    private BigDecimal finalAmount;


    public static enum Status {
        DRAFT,
        APPROVED,
        REJECTED
    }
}
