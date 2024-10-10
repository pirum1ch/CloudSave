package ru.pirum1ch.cloudsave.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.pirum1ch.cloudsave.models.File;
import ru.pirum1ch.cloudsave.repositories.FileRepo;
import ru.pirum1ch.cloudsave.utils.FileManager;

import java.io.IOException;

@Service
public class FileService {

    private FileRepo fileRepo;
    private FileManager fileManager;

    public FileService(FileRepo fileRepo, FileManager fileManager) {
        this.fileRepo = fileRepo;
        this.fileManager = fileManager;
    }

    public File upload (MultipartFile file) throws IOException {
        String key = fileManager.generateKey(file.getName());
        File uploadedFile = File.builder()
                .name(file.getName())
                .key(key)
                .size(file.getSize())
                .extention(file.getContentType())
                .build();
        fileRepo.save(uploadedFile);
        fileManager.fileUpload(file.getBytes(), file.getName());
        return uploadedFile;
    }
}
