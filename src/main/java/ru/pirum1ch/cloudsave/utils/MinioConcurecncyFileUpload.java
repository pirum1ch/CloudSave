package ru.pirum1ch.cloudsave.utils;

import io.minio.MinioAsyncClient;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.pirum1ch.cloudsave.configurations.MinioConfig;

import java.io.ByteArrayInputStream;

@Log4j2
public class MinioConcurecncyFileUpload implements Runnable {

    private final MinioAsyncClient minioAsyncClient;
    private final byte [] file;
    private final String key;
    private final String contentType;

    public MinioConcurecncyFileUpload(MinioAsyncClient minioAsyncClient, byte [] file, String contentType, String key) {
        this.minioAsyncClient = minioAsyncClient;
        this.file = file;
        this.key = key;
        this.contentType = contentType;
    }

    @Override
    public void run() {
        try{
            log.info(Thread.currentThread().getName());
            minioAsyncClient.putObject(PutObjectArgs.builder()
                .bucket(MinioConfig.getUploadBucketName())
                .object(key)
                .contentType(contentType)
                .stream(new ByteArrayInputStream(file), file.length, -1)
                .build());
        }catch (Exception e){
            log.info("Ошибка " + e.getMessage());
            e.printStackTrace();
        }
    }
}
