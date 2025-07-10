package com.medhir.rest.controller.accountantModule;

import com.medhir.rest.model.accountantModule.PurchaseOrderModel;
import com.medhir.rest.service.accountantModule.PurchaseOrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/purchase-orders")
public class PurchaseOrderController {
    private final PurchaseOrderService purchaseOrderService;

    @Autowired
    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PurchaseOrderModel> createPurchaseOrder(
            @RequestPart("purchaseOrder") String purchaseOrderJson,
            @RequestPart(value = "attachment", required = false) MultipartFile attachment
    ) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        PurchaseOrderModel purchaseOrder = mapper.readValue(purchaseOrderJson, PurchaseOrderModel.class);
        PurchaseOrderModel created = purchaseOrderService.createPurchaseOrder(purchaseOrder, attachment);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrderModel>> getAllPurchaseOrders() {
        List<PurchaseOrderModel> orders = purchaseOrderService.getAllPurchaseOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderModel> getPurchaseOrderById(@PathVariable String id) {
        return purchaseOrderService.getPurchaseOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 