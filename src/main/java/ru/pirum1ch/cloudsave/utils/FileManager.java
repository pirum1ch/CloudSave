package ru.pirum1ch.cloudsave.utils;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Component
@Log4j2
public class FileManager {
    //Директоря для загрузки. Подгружается из файла пропертей
    @Value("${directory.to.load}")
    private String loadDiretory;

    /**
     * Загрузка файла. Для каждого файла генерится отдельный уникальный ключ, под которым он сохранятеся в облаке.
     * Данные о загруденном файле созранятеся в БД.
     *
     * @param resource
     * @param key
     * @throws IOException
     * @throws InvalidPathException
     */
    public void fileUpload(byte[] resource, String key) throws IOException, InvalidPathException {
        Path path = Paths.get(loadDiretory, key).toAbsolutePath();
        log.log(Level.INFO, "Адрес сохранения файла: " + path);

        Path file = Files.createFile(path);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file.toString());
            fileOutputStream.write(resource);
            log.log(Level.INFO, "Файл загружен успешно!");
        } finally {
            log.log(Level.INFO, "Закрываем поток записи файла.");
            assert fileOutputStream != null;
            fileOutputStream.close();
        }
    }

    /**
     * Скачиванеи файла. Тут все элеметнтарно
     *
     * @param key
     * @return
     * @throws IOException
     */
    public Resource download(String key) throws IOException {
        Path path = Paths.get(loadDiretory + key);
        log.log(Level.INFO, "Адрес скачивания фалйа: " + path);
        Resource resource = new UrlResource(path.toUri());
        if (resource.exists() || resource.isReadable()) {
            log.log(Level.INFO, "Файл скачан успешно!");
            return resource;
        } else {
            log.log(Level.ERROR, "Ошибка скачивания!");
            throw new IOException();
        }
    }

    /**
     * Удаление файла
     *
     * @param key
     * @throws IOException
     */
    public void deleteFile(String key) throws IOException {
        Path path = Paths.get(loadDiretory + key);
        log.log(Level.INFO, "Директория для удаления файла: " + path);

        Resource resource = new UrlResource(path.toUri());
        if (resource.exists() || resource.isFile()) {
            Files.delete(path);
            log.log(Level.INFO, "Удаляем файл из директории");
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
