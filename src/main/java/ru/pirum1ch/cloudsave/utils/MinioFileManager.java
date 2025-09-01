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

    //Директория для загрузки.
    private String loadDiretory = MinioConfig.getUploadBucketName();

//    /**
//     * Загрузка файла. Для каждого файла генерится отдельный уникальный ключ, под которым он сохранятеся в облаке.
//     * Данные о загруденном файле созранятеся в БД.
//     *
//     * @param resource
//     * @param key
//     * @throws IOException
//     * @throws InvalidPathException
//     */
//    public void fileUpload(byte[] resource, String key) throws IOException, InvalidPathException {
//        Path path = Paths.get(loadDiretory, key).toAbsolutePath();
//        log.log(Level.INFO, "Адрес сохранения файла: " + path);
//
//        Path file = Files.createFile(path);
//        FileOutputStream fileOutputStream = null;
//        try {
//            fileOutputStream = new FileOutputStream(file.toString());
//            fileOutputStream.write(resource);
//            log.log(Level.INFO, "Файл загружен успешно!");
//        } finally {
//            log.log(Level.INFO, "Закрываем поток записи файла.");
//            assert fileOutputStream != null;
//            fileOutputStream.close();
//        }
//    }


    public void minioUpload(MultipartFile file, String key) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException {
        //TODO Return a name of bucket
        log.log(Level.INFO, "Сохраняем файл в minio: " + loadDiretory);
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(loadDiretory)
                .object(key)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build());
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
        }
//
//
//
//
//        Path path = Paths.get(loadDiretory +"/"+ key);
//        log.log(Level.INFO, "Адрес скачивания фалйа: " + path);
//        Resource resource = new UrlResource(path.toUri());
//        if (resource.exists() || resource.isReadable()) {
//            log.log(Level.INFO, "Файл скачан успешно!");
//            return resource;
//        } else {
//            log.log(Level.ERROR, "Ошибка скачивания!");
//            throw new IOException();
//        }
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
                minioClient.statObject(StatObjectArgs.builder().bucket(loadDiretory).object(key).build());
            } catch (ErrorResponseException errorResponseException) {
                log.log(Level.ERROR, "Файл " + key + " не найден:\n" + errorResponseException.getLocalizedMessage());
            }
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(loadDiretory).object(key).build());
        }
    }

    /**
     * Генерация уникального ключа
     *
     * @param name
     * @return
     */
    public String generateKey(String name) {
        return DigestUtils.md5DigestAsHex((name + LocalDateTime.now()).getBytes());
    }
}
