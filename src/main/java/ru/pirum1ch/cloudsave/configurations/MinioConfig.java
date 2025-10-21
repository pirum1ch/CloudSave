package ru.pirum1ch.cloudsave.configurations;

import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    public static String UPLOAD_BUCKET_NAME;
//    public static final String UPLOAD_BUCKET_NAME = "test-bucket";

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.access.name}")
    private String minioAccessName;

    @Value("${minio.access.secret}")
    private String minioAccessSecret;

    @Bean
    public MinioClient minioClient() throws MinioException{
        try {
            return MinioClient.builder()
                    .endpoint(minioUrl)
                    .credentials(minioAccessName, minioAccessSecret)
                    .build();
        }catch (Exception exception){
            throw new MinioException("Ошибка инициализации клиента Minio");
        }
    }

    public static String getUploadBucketName() {
        return UPLOAD_BUCKET_NAME;
    }

    @Value("${minio.bucket.name}")
    public void setUploadBucketName(String uploadBucketName) {
        UPLOAD_BUCKET_NAME = uploadBucketName;
    }

}
