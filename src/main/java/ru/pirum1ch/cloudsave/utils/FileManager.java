package ru.pirum1ch.cloudsave.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Component
public class FileManager {
//    String directoryToLoad = "C:\\Develop\\study\\CloudSave\\src\\main\\resources\\load\\";
//    String directoryToLoad = "\\src\\main\\resources\\load";

    //TODO вывести в проперти
    private final String DIRECTORY_TO_LOAD = "src/main/resources/load/";

    public void fileUpload(byte[] resource, String key) throws IOException, InvalidPathException {
        Path path = Paths.get(DIRECTORY_TO_LOAD, key).toAbsolutePath();
        Path file = Files.createFile(path);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file.toString());
            fileOutputStream.write(resource);
        } finally {
            fileOutputStream.close();
        }
    }

    public Resource download(String key) throws IOException {
        Path path = Paths.get(DIRECTORY_TO_LOAD + key);
        Resource resource = new UrlResource(path.toUri());
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new IOException();
        }
    }

    public void deleteFile(String key) throws IOException {
        Path path = Paths.get(DIRECTORY_TO_LOAD + key);
        Resource resource = new UrlResource(path.toUri());
        if (resource.exists() || resource.isFile()) {
            Files.delete(path);
        }
    }

    public String generateKey(String name) {
        return DigestUtils.md5DigestAsHex((name + LocalDateTime.now()).getBytes());
    }


}
