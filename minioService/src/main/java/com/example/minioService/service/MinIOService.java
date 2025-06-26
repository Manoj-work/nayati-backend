package com.example.minioService.service;

import com.example.minioService.util.SnowflakeIDGenerator;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.BucketExistsArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
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
}

