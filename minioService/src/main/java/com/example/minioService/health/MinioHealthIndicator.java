package com.example.minioService.health;

import io.minio.MinioClient;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Component
@RequiredArgsConstructor
public class MinioHealthIndicator implements HealthIndicator {

    private final MinioClient minioClient;

    private static final String HEALTH_BUCKET = "health-check-bucket";
    private static final String HEALTH_OBJECT = "healthcheck.txt";

    @Override
    public Health health() {
        try {
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(HEALTH_BUCKET).build()
            );

            if (!bucketExists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(HEALTH_BUCKET).build()
                );
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(HEALTH_BUCKET)
                            .object(HEALTH_OBJECT)
                            .stream(new ByteArrayInputStream("ok".getBytes()), "ok".length(), -1)
                            .contentType("text/plain")
                            .build()
            );

            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(HEALTH_BUCKET)
                            .object(HEALTH_OBJECT)
                            .build()
            );

            return Health.up().withDetail("minio", "Upload and fetch test OK").build();

        } catch (Exception e) {
            return Health.down(e).withDetail("minio", "Failed: " + e.getMessage()).build();
        }
    }
}
