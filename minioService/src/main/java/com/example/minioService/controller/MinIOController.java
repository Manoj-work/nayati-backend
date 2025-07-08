package com.example.minioService.controller;

import com.example.minioService.service.MinIOService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/minio")
@RequiredArgsConstructor
public class MinIOController {

    private final MinIOService minIOService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("bucketName") String bucketName,
            @RequestParam("filePath") String filePath,  // 🔥 Pre-generated file path
            @RequestParam("file") MultipartFile file) {
        String fileUrl = minIOService.uploadFile(bucketName, filePath, file);
        return ResponseEntity.ok(fileUrl);
    }

    @GetMapping("/generate-uuid")
    public ResponseEntity<String> generateUUID() {
        String generatedId = minIOService.generateUniqueID();
        return ResponseEntity.ok(generatedId);
    }
}