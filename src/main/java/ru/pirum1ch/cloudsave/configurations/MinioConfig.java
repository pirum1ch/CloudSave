package ru.pirum1ch.cloudsave.configurations;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${minio.bucket.name}")
    public static String UPLOAD_BUCKET_NAME;
//    public static final String UPLOAD_BUCKET_NAME = "test-bucket";

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.access.name}")
    private String minioAccessName;

    @Value("${minio.access.secret}")
    private String minioAccessSecret;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(minioAccessName, minioAccessSecret)
                .build();
    }

}
