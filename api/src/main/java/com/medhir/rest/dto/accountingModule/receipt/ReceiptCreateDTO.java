package com.medhir.rest.dto.accountingModule.receipt;

import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptCreateDTO {

    @NotBlank
    private String projectId;

    @NotBlank
    private String customerId;

    @NotBlank
    private String receiptNumber;

    @NotBlank
    private String receiptDate;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amountReceived;

    private String paymentMethod;
    private String bankAccountId;
    private String paymentTransactionId;

    @Builder.Default
    private List<LinkedInvoiceDTO> linkedInvoices = List.of();  // Optional at creation

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LinkedInvoiceDTO {
        @NotBlank
        private String invoiceNumber;  // Link by invoice number

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        private BigDecimal amountAllocated;
    }
}
