package ru.pirum1ch.cloudsave.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Component
public class FileManager {
    String directoryToLoad = "/resources/upload";


    public void fileUpload(byte[] resource, String fileName) throws IOException{

        try (FileOutputStream fileOutputStream = new FileOutputStream(
                Files.createFile(
                        Paths.get(directoryToLoad, fileName)
                ).toString()))
        {
//            Path path = Paths.get(directoryToLoad, fileName);
//            Path file = Files.createFile(path);
//            fileOutputStream = new FileOutputStream(file.toString());

            fileOutputStream.write(resource);
        }
    }


    public String generateKey(String name) {
        return DigestUtils.md5DigestAsHex((name + LocalDateTime.now()).getBytes());
    }

}
