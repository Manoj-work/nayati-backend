package com.medhir.rest.controller.accountantModule;

import com.medhir.rest.model.accountantModule.PaymentModel;
import com.medhir.rest.service.accountantModule.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import com.medhir.rest.dto.PaymentDTO;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentModel> getPaymentByPaymentId(@PathVariable String paymentId) {
        PaymentModel payment = paymentService.getPaymentByPaymentId(paymentId);
        return ResponseEntity.ok(payment);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPayment(
            @RequestPart("payment") String paymentJson,
            @RequestPart(value = "paymentProof", required = false) MultipartFile paymentProof) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        PaymentModel payment = mapper.readValue(paymentJson, PaymentModel.class);
        PaymentModel saved = paymentService.createPayment(payment, paymentProof);
        return ResponseEntity.ok(java.util.Map.of(
            "message", "Payment created successfully",
            "paymentId", saved.getPaymentId()
            // "paymentProofUrl", saved.getPaymentProofUrl()
        ));
    }

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments(){
        List<PaymentDTO> paymentList =  paymentService.getAllPaymentDTOs();
        return ResponseEntity.ok(paymentList);
    }
} 