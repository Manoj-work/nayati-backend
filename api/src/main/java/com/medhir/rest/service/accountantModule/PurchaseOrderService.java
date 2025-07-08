package com.medhir.rest.service.accountantModule;

import com.medhir.rest.model.accountantModule.PurchaseOrderModel;
import com.medhir.rest.repository.accountantModule.PurchaseOrderRepository;
import com.medhir.rest.service.CompanyService;
import com.medhir.rest.utils.MinioService;
import com.medhir.rest.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.medhir.rest.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

@Service
public class PurchaseOrderService {
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final MinioService minioService;
    private final VendorService vendorService;
    private final CompanyService companyService;
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    @Autowired
    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository, MinioService minioService, VendorService vendorService, CompanyService companyService, SnowflakeIdGenerator snowflakeIdGenerator) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.minioService = minioService;
        this.vendorService = vendorService;
        this.companyService = companyService;
        this.snowflakeIdGenerator = snowflakeIdGenerator;
    }

    public PurchaseOrderModel createPurchaseOrder(PurchaseOrderModel purchaseOrder, MultipartFile attachment) {

        // Validate vendor and company
        companyService.getCompanyById(purchaseOrder.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + purchaseOrder.getCompanyId()));

        vendorService.getVendorById(purchaseOrder.getVendorId());

        // Generate purchase order ID
        purchaseOrder.setPurchaseOrderId("PO" + snowflakeIdGenerator.nextId());

        // Handle attachment upload
        if (attachment != null && !attachment.isEmpty()) {
            String url = minioService.uploadBillAttachment(attachment, purchaseOrder.getVendorId());
            purchaseOrder.setAttachmentUrls(Collections.singletonList(url));
        } else {
            purchaseOrder.setAttachmentUrls(Collections.emptyList());
        }

        // Calculate totals
        purchaseOrder.setTotalBeforeGST(purchaseOrder.getPurchaseOrderLineItems().stream()
                .map(PurchaseOrderModel.PurchaseOrderLineItem::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        purchaseOrder.setTotalGST(purchaseOrder.getTotalBeforeGST().multiply(purchaseOrder.getPurchaseOrderLineItems().stream()
                .map(PurchaseOrderModel.PurchaseOrderLineItem::getGstPercent)
                .reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(100))));

        purchaseOrder.setFinalAmount(purchaseOrder.getTotalBeforeGST().add(purchaseOrder.getTotalGST()));

        // Set status to DRAFT
        purchaseOrder.setStatus(PurchaseOrderModel.Status.DRAFT);

        return purchaseOrderRepository.save(purchaseOrder);
    }

    public List<PurchaseOrderModel> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    public Optional<PurchaseOrderModel> getPurchaseOrderById(String id) {
        return purchaseOrderRepository.findById(id);
    }
} 