package com.example.minioService.controller;

import com.example.minioService.service.MinIOService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

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

    // 🔥 NEW: File download endpoint
    @GetMapping("/download/{bucketName}/{filePath:.+}")
    public ResponseEntity<InputStreamResource> downloadFile(
            @PathVariable String bucketName,
            @PathVariable String filePath) {
        try {
            InputStream inputStream = minIOService.downloadFile(bucketName, filePath);
            InputStreamResource resource = new InputStreamResource(inputStream);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.substring(filePath.lastIndexOf('/') + 1) + "\"");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 🔥 NEW: File preview endpoint (for images, PDFs, etc.)
    @GetMapping("/preview/{bucketName}/{filePath:.+}")
    public ResponseEntity<InputStreamResource> previewFile(
            @PathVariable String bucketName,
            @PathVariable String filePath) {
        try {
            InputStream inputStream = minIOService.downloadFile(bucketName, filePath);
            InputStreamResource resource = new InputStreamResource(inputStream);
            
            // Determine content type based on file extension
            String contentType = minIOService.getContentType(filePath);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 🔥 NEW: Get file URL for frontend access
    @GetMapping("/url/{bucketName}/{filePath:.+}")
    public ResponseEntity<String> getFileUrl(
            @PathVariable String bucketName,
            @PathVariable String filePath) {
        try {
            String fileUrl = minIOService.getFileUrl(bucketName, filePath);
            return ResponseEntity.ok(fileUrl);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}