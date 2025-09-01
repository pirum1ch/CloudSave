package ru.pirum1ch.cloudsave.services;

import io.minio.errors.*;
import jakarta.persistence.PersistenceException;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.pirum1ch.cloudsave.dto.FileDto;
import ru.pirum1ch.cloudsave.models.File;
import ru.pirum1ch.cloudsave.repositories.FileRepo;
import ru.pirum1ch.cloudsave.utils.FileManager;
import ru.pirum1ch.cloudsave.utils.MinioFileManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

@Service
@Log4j2
public class MinioFileService {

    private final FileRepo fileRepo;
    private final MinioFileManager fileManager;

    public MinioFileService(FileRepo fileRepo, MinioFileManager fileManager) {
        this.fileRepo = fileRepo;
        this.fileManager = fileManager;
    }

    public File getFileByName(String filename) {
        return fileRepo.findByName(filename);
    }

    public List<FileDto> getListOfAllFiles(int limit) throws PersistenceException {
        log.log(Level.INFO, "Проверяем аргументы на валидность");
        if (limit <= 0) {
            throw new NumberFormatException("В качестве лимита передан 0 или отрицательное число. Введите положительно целое число.");
        }

        List<File> list = fileRepo.findAll();
        int listsize = list.size();
        List<FileDto> listdto = new LinkedList<>();

        if (listsize < limit) {
            limit = listsize;
        }

        for (int i = 0; i < limit; i++) {
            listdto.add(FileDto.builder()
                    .filename(list.get(i).getName())
                    .size(list.get(i).getSize())
                    .build());
        }

        log.log(Level.INFO, "Найдены файлы: \n" + list);
        return listdto;
    }

    //TODO How to upload a few files
    @Transactional(rollbackFor = {IOException.class, MinioException.class, NoSuchAlgorithmException.class, InvalidKeyException.class})
    public File upload(String fileName, MultipartFile file) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException {
        log.log(Level.INFO, "Проверяем что входящие аргументы не пустые");
        if (fileName.isEmpty() || file.isEmpty()) {
            throw new IllegalArgumentException("Переданные на вход значения пусты!");
        }
        String key = fileManager.generateKey(file.getName());
        log.log(Level.INFO, "Создали новый ключ для файла: " + key);
        if (fileRepo.findByName(fileName) != null) {
            log.log(Level.INFO, "Файл с таким именем уже есть");
//            fileName = fileName + "_" + new SimpleDateFormat("HH:mm:ss").format(new Date().getTime());
            fileName = new StringBuilder(fileName).
                    insert(
                            fileName.indexOf("."),
                            "_" + new SimpleDateFormat("HH:mm:ss")
                                    .format(new Date().getTime())).toString();
        }

        File uploadedFile = File.builder()
                .name(fileName)
                .key(key)
                .size(file.getSize())
                .extention(file.getContentType())
                .uploadDate(new Date())
                .build();

        log.log(Level.INFO, "Создали новый объект файла: \n" + uploadedFile);
        fileRepo.save(uploadedFile);

        log.log(Level.INFO, "Сохранили данные сущности в БД");
        fileManager.minioUpload(file, key);

        return uploadedFile;
    }

    public void download(String fileName)
            throws IOException, ServerException, InsufficientDataException, ErrorResponseException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        log.log(Level.INFO, "Проверяем аргументы на валидность: ");
        if (fileName.isEmpty()) {
            throw new IllegalArgumentException("Переданные на вход занчения пусты!");
        }

        String key = fileRepo.findByName(fileName).getKey();
        log.log(Level.INFO, "Найден файл для скачивания!");
        fileManager.download(key, fileName);
    }

    public File fileNameUpdate(String fileName, String name) throws IllegalArgumentException, IOException {
        log.log(Level.INFO, "Проверяем аргументы на валидность");
        if (fileName.isEmpty() || name.isEmpty()) {
            throw new IllegalArgumentException("Переданные на вход значения пусты!");
        }

        File foundFile = fileRepo.findByName(fileName);
        log.log(Level.INFO, "Файл для обновления: " + foundFile);
        if (foundFile == null) {
            throw new FileNotFoundException("Нет такого файла!");
        }

        String nameNew = name.substring(13, 20);
        log.log(Level.INFO, "Получили новое имя файла: " + nameNew);
        foundFile.setName(nameNew);
        log.log(Level.INFO, "Обновили файл: " + foundFile);
        fileRepo.save(foundFile);
        log.log(Level.INFO, "");
        return foundFile;
    }

    @Transactional(rollbackFor = {IOException.class,
            FileNotFoundException.class,
            ServerException.class,
            InsufficientDataException.class,
            ErrorResponseException.class,
            NoSuchAlgorithmException.class,
            InvalidKeyException.class,
            InvalidResponseException.class,
            XmlParserException.class,
            InternalException.class,})
    public void deleteFile(String filename) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        File file = null;
        log.log(Level.INFO, "Проверяем что аргументы валидны.");
        if (filename.isEmpty()) {
            log.log(Level.INFO, "Входящий аргумент пустой! Введите имя файла для удаления.");
            throw new IllegalArgumentException();
        }

//        try {
        //Ищем файл и передаем его в объект File
//            try {
                file = fileRepo.findByName(filename);
                if (file == null || file.getSize() == 0) {
                    log.log(Level.INFO, "Такого файла не существует! ");
                    throw new FileNotFoundException();
                }
                log.log(Level.INFO, "Найден файл для удаления:\n" + file);
//            } catch (IOException | NullPointerException exception) {
//                log.log(Level.ERROR, exception.getLocalizedMessage());
//            }

            fileManager.deleteFile(file);
            fileRepo.delete(file);
            log.log(Level.INFO, "Файл удален.");
//        } catch (IOException |
//                 ServerException |
//                 InsufficientDataException |
//                 ErrorResponseException |
//                 NoSuchAlgorithmException |
//                 InvalidKeyException |
//                 InvalidResponseException |
//                 XmlParserException |
//                 InternalException exception) {
//            log.log(Level.ERROR, "Ошибка удаления файла:\n" + exception.getLocalizedMessage());
//        }

    }
}
