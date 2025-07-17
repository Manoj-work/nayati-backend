package com.medhir.rest.dto.accountantModule.invoice;

import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceCreateDTO {

    @NotBlank(message = "ProjectId is required")
    private String projectId;

    @NotBlank(message = "Customer Id is required")
    private String customerId;

    @NotBlank(message = "Invoice number is required")
    private String invoiceNumber;

    @NotBlank(message = "Invoice date is required")
    private String invoiceDate; // String is fine if you're parsing elsewhere

    @NotBlank(message = "Due date is required")
    private String dueDate;

    @NotEmpty(message = "At least one invoice item is required")
    private List<InvoiceItemDTO> items;

    @NotNull(message = "Subtotal is required")
    private BigDecimal subtotal;

//    @NotNull(message = "Total GST is required")
    private BigDecimal totalGst;

    @NotNull(message = "Total amount is required")
    private BigDecimal totalAmount;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InvoiceItemDTO {
        @NotBlank(message = "Item name is required")
        private String itemName;

        private String description;

        private String hsnOrSac;

        @Min(value = 1, message = "Quantity must be at least 1")
        private int quantity;

        @NotBlank(message = "UOM is required")
        private String uom;

        @DecimalMin(value = "0.0", inclusive = false, message = "Rate must be greater than 0")
        private BigDecimal rate;

        @DecimalMin(value = "0.0", message = "GST % cannot be negative")
        private BigDecimal gstPercentage;

        private BigDecimal total; // line total provided by frontend
    }

    private List<LinkedReceiptDTO> linkedReceipts;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LinkedReceiptDTO {
        private String receiptNumber;
        private BigDecimal amountAllocated;
    }

}
