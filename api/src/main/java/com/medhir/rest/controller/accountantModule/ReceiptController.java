package com.medhir.rest.controller.accountantModule;

import com.medhir.rest.dto.accountantModule.receipt.ReceiptCreateDTO;
import com.medhir.rest.dto.accountantModule.receipt.ReceiptResponse;
import com.medhir.rest.dto.accountantModule.receipt.UnallocatedReceiptsResponse;
import com.medhir.rest.model.accountantModule.Receipt;
import com.medhir.rest.service.accountantModule.ReceiptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    @PostMapping
    public ResponseEntity<?> createReceipt(@Valid @RequestBody ReceiptCreateDTO request) {
        Receipt savedReceipt = receiptService.createReceipt(request);

        return ResponseEntity.ok(Map.of(
                "message", "Receipt created successfully!",
                "receiptId", savedReceipt.getId(),
                "allocatedAmount", savedReceipt.getAllocatedAmount(),
                "unallocatedAmount", savedReceipt.getUnallocatedAmount()
        ));
    }

    @GetMapping("/{receiptNumber}")
    public ResponseEntity<ReceiptResponse> getReceiptByNumber(@PathVariable String receiptNumber) {
        return ResponseEntity.ok(receiptService.getReceiptByNumber(receiptNumber));
    }

    @GetMapping
    public ResponseEntity<List<ReceiptResponse>> getAllReceipts() {
        return ResponseEntity.ok(receiptService.getAllReceipts());
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ReceiptResponse>> getReceiptsByProject(@PathVariable String projectId) {
        return ResponseEntity.ok(receiptService.getReceiptsByProjectId(projectId));
    }
    @GetMapping("/unallocated/project/{projectId}")
    public ResponseEntity<UnallocatedReceiptsResponse> getUnallocatedReceipts(@PathVariable String projectId) {
        return ResponseEntity.ok(receiptService.getUnallocatedReceipts(projectId));
    }

}
