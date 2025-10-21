package ru.pirum1ch.cloudsave.utils;

import io.minio.*;
import io.minio.errors.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.pirum1ch.cloudsave.configurations.AsyncConfig;
import ru.pirum1ch.cloudsave.configurations.MinioConfig;
import ru.pirum1ch.cloudsave.models.File;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


@Component
@Log4j2
@Async
public class MinioFileManager {

    private final MinioClient minioClient;

    private final MinioAsyncClient minioAsyncClient;

    public MinioFileManager(MinioClient minioClient, MinioAsyncClient minioAsyncClient) {
        this.minioClient = minioClient;
        this.minioAsyncClient = minioAsyncClient;
    }

    private final String loadDirectory = MinioConfig.getUploadBucketName();

    /**
     * Загрузка файла в хранилище
     *
     * @param file
     * @param key
     * @throws IOException
     * @throws MinioException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public void minioUpload(MultipartFile file, String key) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException {
        //TODO Return a name of bucket
        log.info("Сохраняем файл в minio: " + loadDirectory);

//        Executor executor = new AsyncConfig().taskExecutor();
//        executor.execute(() -> new MinioConcurecncyFileUpload(minioAsyncClient, file, key).run());
        minioAsyncClient.putObject(PutObjectArgs.builder()
                .bucket(loadDirectory)
                .object(key)
                .contentType(file.getContentType())
                .stream(file.getInputStream(), file.getSize(), -1)
                .build());
//        minioClient.putObject(PutObjectArgs.builder().
//                bucket(loadDiretory).
//                object(key).
//                stream(file.getInputStream(), file.getSize(), -1).
//                contentType(file.getContentType())
//                .build());
        log.info("Файл успешно загружен в minio");
    }

    /**
     * Скачиванеи файла. Тут все элементарно
     *
     * @param key
     * @return
     * @throws IOException
     */
    public void download(String key, String fileName) throws IllegalArgumentException, IOException, ServerException,
            InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        if (key == null || fileName == null) {
            throw new IllegalArgumentException();
        }

        minioClient.downloadObject(DownloadObjectArgs.builder().bucket(loadDirectory).object(key).filename("/Users/dmitriy.pirumov/Downloads/" + fileName).build());
        log.info("Файл успешно скачан в директорию: " + loadDirectory);

    }

    /**
     * Удаление файла
     *
     * @param file
     * @throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
     */
    public void deleteFile(File file) throws IOException, ServerException, InsufficientDataException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException, ErrorResponseException {
        String key = file.getKey();
        Path path = Paths.get(loadDirectory + " Key: " + key);
        log.info("Директория для удаления файла: " + path);

        //Проверяем что файл существует
        if (minioClient.bucketExists(BucketExistsArgs.builder().bucket(loadDirectory).build())) {
            try {
                if (minioClient.statObject(StatObjectArgs.builder().bucket(loadDirectory).object(key).build()) != null) {
                    minioClient.removeObject(RemoveObjectArgs.builder().bucket(loadDirectory).object(key).build());
                }
            } catch (FileNotFoundException fileNotFoundException) {
                log.info("Файл " + key + " не найден:\n" + fileNotFoundException.getLocalizedMessage());
            }
        } else {
            throw new IOException();
        }
    }

    /**
     * Генерация уникального ключа
     *
     * @param name
     * @return
     */
//    public CompletableFuture<String> generateKey(String name) {
//        String key = DigestUtils.md5DigestAsHex((name + LocalDateTime.now()).getBytes());
//        log.info("Создали новый ключ для файла " + key);
//        return CompletableFuture.completedFuture(key);
//    }

    public String generateKey(String name) {
        String key = DigestUtils.md5DigestAsHex((name + LocalDateTime.now()).getBytes());
        log.info("Создали новый ключ для файла " + key);
        return key;
    }
}

