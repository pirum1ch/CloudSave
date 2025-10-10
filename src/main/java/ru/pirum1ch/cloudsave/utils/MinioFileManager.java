package ru.pirum1ch.cloudsave.utils;

import io.minio.*;
import io.minio.errors.*;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.pirum1ch.cloudsave.configurations.MinioConfig;
import ru.pirum1ch.cloudsave.models.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;


@Component
@Log4j2
public class MinioFileManager {

    private final MinioClient minioClient;

    public MinioFileManager(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    private String loadDiretory = MinioConfig.getUploadBucketName();

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
        log.log(Level.INFO, "Сохраняем файл в minio: " + loadDiretory);
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(loadDiretory)
                .object(key)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build());
        log.log(Level.INFO, "Файл сохранен успешно");
    }


    /**
     * Скачиванеи файла. Тут все элеметнтарно
     *
     * @param key
     * @return
     * @throws IOException
     */
    public void download(String key, String fileName) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if (key != null) {
            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket(loadDiretory)
                            .object(key)
                            .filename("/Users/dmitriy.pirumov/Downloads/" + fileName)
                            .build()
            );
            log.log(Level.INFO, "Файл успешно скачан в директорию: " + loadDiretory);
        }
    }

    /**
     * Удаление файла
     *
     * @param file
     * @throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
     */
    public void deleteFile(File file) throws IOException, ServerException, InsufficientDataException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, ErrorResponseException {
        String key = file.getKey();
        Path path = Paths.get(loadDiretory + " Key: " + key);
        log.log(Level.INFO, "Директория для удаления файла: " + path);

        //Проверяем что файл существует
        if (minioClient.bucketExists(BucketExistsArgs.builder().bucket(loadDiretory).build())) {
            try {
                if (minioClient.statObject(StatObjectArgs.builder().bucket(loadDiretory).object(key).build()) != null){
                    minioClient.removeObject(RemoveObjectArgs.builder().bucket(loadDiretory).object(key).build());
                }
            } catch (FileNotFoundException fileNotFoundException) {
                log.log(Level.INFO, "Файл " + key + " не найден:\n" + fileNotFoundException.getLocalizedMessage());
            }

        }else {
            throw new IOException();
        }
    }

    /**
     * Генерация уникального ключа
     *
     * @param name
     * @return
     */
    public String generateKey(String name) {
        String key = DigestUtils.md5DigestAsHex((name + LocalDateTime.now()).getBytes());
        log.log(Level.INFO, "Создали новый ключ для файла " + key);
        return key;
    }
}
