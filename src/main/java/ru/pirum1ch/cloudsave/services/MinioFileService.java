package ru.pirum1ch.cloudsave.services;

import io.jsonwebtoken.ExpiredJwtException;
import io.minio.errors.*;
import jakarta.persistence.PersistenceException;
import jakarta.validation.constraints.Min;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.pirum1ch.cloudsave.dto.FileDto;
import ru.pirum1ch.cloudsave.models.File;
import ru.pirum1ch.cloudsave.repositories.FileListRepo;
import ru.pirum1ch.cloudsave.repositories.FileRepo;
import ru.pirum1ch.cloudsave.utils.MinioFileManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;


@Service
@Log4j2
public class MinioFileService {

    private final FileRepo fileRepo;
    private final FileListRepo fileListRepo;
    private final MinioFileManager fileManager;

    public MinioFileService(FileRepo fileRepo, FileListRepo fileListRepo, MinioFileManager fileManager) {
        this.fileRepo = fileRepo;
        this.fileListRepo = fileListRepo;
        this.fileManager = fileManager;
    }

    public Page<File> getListOfAllFiles(Pageable pageable) throws PersistenceException {
        return fileListRepo.findAll(pageable);
    }

    @Async("taskExecutor")
    @Transactional(rollbackFor = {IOException.class, MinioException.class, NoSuchAlgorithmException.class, InvalidKeyException.class})
    public void upload(@NonNull MultipartFile[] multiFile)
            throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException, ExecutionException, InterruptedException {

        for (MultipartFile file : multiFile) {
            try {
                String fileName = file.getOriginalFilename();
                String key = fileManager.generateKey(fileName);

                if (fileRepo.findByName(fileName) != null) {
                    log.info("Файл с таким именем уже есть");
                    fileName = new StringBuilder(fileName)
                            .insert(fileName.indexOf("."), "_" + new SimpleDateFormat("HH:mm:ss")
                                    .format(new Date().getTime()))
                            .toString();
                }

                File uploadedFile = File.builder()
                        .name(fileName)
                        .key(key)
                        .size(file.getSize())
                        .extention(file.getContentType())
                        .uploadDate(new Date())
                        .build();
                log.debug("Создали новый объект файла: \n" + uploadedFile);
                fileManager.minioUpload(file, key);
                fileRepo.save(uploadedFile);
                log.info("Файл сохранен успешно");
            }catch (Exception e){
                log.info(e.getLocalizedMessage());
            }
        }
    }

    public void download(@NonNull String fileName)
            throws IOException, ServerException, InsufficientDataException, ErrorResponseException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        String key = fileRepo.findByName(fileName).getKey();
        log.info("Найден файл для скачивания!");
        fileManager.download(key, fileName);
    }

    public File fileNameUpdate(@NonNull String fileName, @NonNull String name) throws IllegalArgumentException, IOException {

        File foundFile = fileRepo.findByName(fileName);
        log.info("Файл для обновления: " + foundFile);
        if (foundFile == null) {
            throw new FileNotFoundException("Нет такого файла!");
        }

        String nameNew = name.substring(13, 20);
        log.info("Получили новое имя файла: " + nameNew);
        foundFile.setName(nameNew);
        log.info("Обновили файл: " + foundFile);
        fileRepo.save(foundFile);
        log.info("");
        return foundFile;
    }

    @Transactional(rollbackFor = {IOException.class, ExpiredJwtException.class})
    public void deleteFile(String filename) throws ExpiredJwtException, IOException,
            ServerException,
            InsufficientDataException,
            ErrorResponseException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            InvalidResponseException,
            XmlParserException,
            InternalException {
        log.info("Проверяем что входящие аргументы не пусты: " + filename);
        if (filename.isEmpty()) {
            log.info("Входящий аргумент пустой! Введите имя файла для удаления.");
            throw new IllegalArgumentException();
        }

        File file = fileRepo.findByName(filename);
        if (fileRepo.findByName(filename) == null) {
            log.info("Файла " + filename + " не существует! ");
            throw new FileNotFoundException();
        }
        log.info("Найден файл для удаления:\n" + file.toString());

        fileManager.deleteFile(file);
        fileRepo.delete(file);
        log.info("Файл удален.");
    }
}
