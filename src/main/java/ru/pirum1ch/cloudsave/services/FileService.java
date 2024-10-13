package ru.pirum1ch.cloudsave.services;

import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.pirum1ch.cloudsave.models.File;
import ru.pirum1ch.cloudsave.repositories.FileRepo;
import ru.pirum1ch.cloudsave.utils.FileManager;

import java.io.IOException;
import java.util.Date;

@Service
public class FileService {

    private FileRepo fileRepo;
    private FileManager fileManager;

    public FileService(FileRepo fileRepo, FileManager fileManager) {
        this.fileRepo = fileRepo;
        this.fileManager = fileManager;
    }

    @Transactional(rollbackFor = {IOException.class})
    public File upload (MultipartFile file) throws IOException {
        Date date = new Date();
        String key = fileManager.generateKey(file.getName());
        File uploadedFile = File.builder()
                .name(file.getOriginalFilename())
                .key(key)
                .size(file.getSize())
                .extention(file.getContentType())
                .uploadDate(date)
                .build();
        fileRepo.save(uploadedFile);
        fileManager.fileUpload(file.getBytes(), key);
        return uploadedFile;
    }
}
