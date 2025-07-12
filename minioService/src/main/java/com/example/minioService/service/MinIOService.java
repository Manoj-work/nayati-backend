package com.example.minioService.service;

import com.example.minioService.util.SnowflakeIDGenerator;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.BucketExistsArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MinIOService {

    private final MinioClient minioClient;
    private final SnowflakeIDGenerator snowflakeIDGenerator;

    @Value("${minio.url}")
    private String minioUrl;

    // ✅ Generate UUID / Snowflake ID
    public String generateUniqueID() {
        return String.valueOf(snowflakeIDGenerator.nextId());
    }

    public String uploadFile(String bucketName, String filePath, MultipartFile file) {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filePath) // 🔥 Using the pre-generated file path
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return minioUrl + "/" + bucketName + "/" + filePath;
        } catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    // 🔥 NEW: Download file from MinIO
    public InputStream downloadFile(String bucketName, String filePath) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filePath)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("File download failed", e);
        }
    }

    // 🔥 NEW: Get file URL for frontend access (using MinIO service endpoints)
    public String getFileUrl(String bucketName, String filePath) {
        // Return URL that goes through our MinIO service for proper CORS and access control
        return "http://192.168.0.200:8085/minio/preview/" + bucketName + "/" + filePath;
    }

    // 🔥 NEW: Get content type based on file extension
    public String getContentType(String filePath) {
        String extension = filePath.substring(filePath.lastIndexOf('.') + 1).toLowerCase();
        
        Map<String, String> contentTypeMap = new HashMap<>();
        contentTypeMap.put("pdf", "application/pdf");
        contentTypeMap.put("jpg", "image/jpeg");
        contentTypeMap.put("jpeg", "image/jpeg");
        contentTypeMap.put("png", "image/png");
        contentTypeMap.put("gif", "image/gif");
        contentTypeMap.put("bmp", "image/bmp");
        contentTypeMap.put("webp", "image/webp");
        contentTypeMap.put("doc", "application/msword");
        contentTypeMap.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        contentTypeMap.put("xls", "application/vnd.ms-excel");
        contentTypeMap.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        contentTypeMap.put("txt", "text/plain");
        contentTypeMap.put("csv", "text/csv");
        contentTypeMap.put("xml", "application/xml");
        contentTypeMap.put("json", "application/json");
        contentTypeMap.put("zip", "application/zip");
        contentTypeMap.put("rar", "application/x-rar-compressed");
        
        return contentTypeMap.getOrDefault(extension, "application/octet-stream");
    }

    // 🔥 NEW: Check if file exists
    public boolean fileExists(String bucketName, String filePath) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filePath)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}