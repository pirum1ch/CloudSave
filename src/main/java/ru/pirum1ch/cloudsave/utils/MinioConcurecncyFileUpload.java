package ru.pirum1ch.cloudsave.utils;

import io.minio.MinioAsyncClient;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.pirum1ch.cloudsave.configurations.MinioConfig;

@Log4j2
public class MinioConcurecncyFileUpload implements Runnable {

    private final MinioAsyncClient minioAsyncClient;
    private final MultipartFile file;
    private final String key;

    public MinioConcurecncyFileUpload(MinioAsyncClient minioAsyncClient, MultipartFile file, String key) {
        this.minioAsyncClient = minioAsyncClient;
        this.file = file;
        this.key = key;
    }

    @Override
    public void run() {
        try{
            log.info(Thread.currentThread().getName());
            minioAsyncClient.putObject(PutObjectArgs.builder()
                .bucket(MinioConfig.getUploadBucketName())
                .object(key)
                .contentType(file.getContentType())
                .stream(file.getInputStream(), file.getSize(), -1)
                .build());
        }catch (Exception e){
            log.info("Ошибка " + e.getMessage());
            e.printStackTrace();
        }
    }
}
