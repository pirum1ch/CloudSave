package ru.pirum1ch.cloudsave.services;

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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
@Log4j2
public class FileService {

    private final FileRepo fileRepo;
    private final FileManager fileManager;

    public FileService(FileRepo fileRepo, FileManager fileManager) {
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

        log.log(Level.INFO, "Найдены файлы: \n"+ list);
        return listdto;
    }

    @Transactional(rollbackFor = {IOException.class})
    public File upload(String fileName, MultipartFile file) throws IOException {
        log.log(Level.INFO, "Проверяем что входящие аргументы не пустые");
        if (fileName.isEmpty() || file.isEmpty()) {
            throw new IllegalArgumentException("Переданные на вход занчения пусты!");
        }
        Date date = new Date();
        String key = fileManager.generateKey(file.getName());
        log.log(Level.INFO, "Создали новый ключ для файла: " + key);
        File uploadedFile = File.builder()
                .name(fileName)
                .key(key)
                .size(file.getSize())
                .extention(file.getContentType())
                .uploadDate(date)
                .build();
        log.log(Level.INFO, "Создали новый объект файла: \n" + uploadedFile);
        fileRepo.save(uploadedFile);
        log.log(Level.INFO, "Сохранили данные сущности в БД");
        fileManager.fileUpload(file.getBytes(), key);
        return uploadedFile;
    }

    public Resource download(String fileName) throws IOException {
        log.log(Level.INFO, "Проверяем аргументы на валидность: ");
        if(fileName.isEmpty()){
            throw new IllegalArgumentException("Переданные на вход занчения пусты!");
        }

        File foundFile = fileRepo.findByName(fileName);
        log.log(Level.INFO, "Найден файл для скачивания!");
        return fileManager.download(foundFile.getKey());
    }

    @Transactional(rollbackFor = {IOException.class})
    public File fileUpdateByName(String fileName, MultipartFile file) throws IOException {
        log.log(Level.INFO, "Проверяем аргументы на валидность");
        if (fileName.isEmpty() || file.isEmpty()) {
            throw new IllegalArgumentException("Переданные на вход занчения пусты!");
        }

        File foundFile = fileRepo.findByName(fileName);
        log.log(Level.INFO, "Проверяем что найденный файл не null");
        if (foundFile == null) {
            throw new NoSuchFileException(fileName);
        }

        String oldFileKey = foundFile.getKey();
        String newFileKey = fileManager.generateKey(file.getOriginalFilename());
        log.log(Level.INFO, "Сформирован новый ключ для файла: " + newFileKey);

        foundFile.setName(fileName);
        foundFile.setKey(newFileKey);
        log.log(Level.INFO, "Обновленны данные файла: " + foundFile);

        fileManager.fileUpload(file.getBytes(), newFileKey);
        fileRepo.save(foundFile);
        fileManager.deleteFile(oldFileKey);
        return foundFile;
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

    @Transactional(rollbackFor = {IOException.class, FileNotFoundException.class})
    public void deleteFile(String filename) throws IOException {
        log.log(Level.INFO, "Проверяем что аргументы валидны.");
        if (filename.isEmpty()){
            log.log(Level.INFO, "Входящий аргумент пустой! ");
            throw new IllegalArgumentException();
        }

        File foundFile = fileRepo.findByName(filename);
        log.log(Level.INFO, "Найден файл для удаления: " + foundFile);
        if (foundFile.getSize() == 0) {
            log.log(Level.INFO, "");
            throw new FileNotFoundException();
        }

        fileManager.deleteFile(foundFile.getKey());
        fileRepo.delete(foundFile);
        log.log(Level.INFO, "Файл удален.");
    }
}
