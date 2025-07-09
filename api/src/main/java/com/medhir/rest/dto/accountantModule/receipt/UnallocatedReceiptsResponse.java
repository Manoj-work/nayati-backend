package com.medhir.rest.dto.accountantModule.receipt;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class UnallocatedReceiptsResponse {
    private List<ReceiptResponse> receipts;
    private BigDecimal totalUnallocatedAmount;
}
