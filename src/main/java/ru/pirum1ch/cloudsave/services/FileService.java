package ru.pirum1ch.cloudsave.services;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Limit;
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
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class FileService {

    private FileRepo fileRepo;
    private FileManager fileManager;

    public FileService(FileRepo fileRepo, FileManager fileManager) {
        this.fileRepo = fileRepo;
        this.fileManager = fileManager;
    }

    public List<FileDto> getListOfAllFiles(int limit) {
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

        return listdto;
    }

    @Transactional(rollbackFor = {IOException.class})
    public File upload(MultipartFile file) throws IOException {
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

    public Resource download(String fileName) throws IOException {
        File foundFile = fileRepo.findByName(fileName);
        return fileManager.download(foundFile.getKey());
    }

    @Transactional(rollbackFor = {IOException.class})
    public String fileUpdateByName(String fileName, MultipartFile file) throws IOException {
        File foundFile = fileRepo.findByName(fileName);
        if (foundFile.getSize() <= 0) {
            throw new NoSuchFileException(fileName);
        }
        String oldFileKey = foundFile.getKey();
        String newFileKey = fileManager.generateKey(file.getName());
        //TODO Не работает
        foundFile.setName(file.getName());
        foundFile.setKey(newFileKey);
        fileManager.fileUpload(file.getBytes(), newFileKey);
        fileRepo.save(foundFile);
        fileManager.deleteFile(oldFileKey);
        return foundFile.getName();
    }

    public File fileNameUpdate(String fileName, String name) throws FileNotFoundException {
        File foundFile = fileRepo.findByName(fileName);
        if (foundFile == null) {
            throw new FileNotFoundException();
        }
        String nameNew = name.substring(13, 20);
        foundFile.setName(nameNew);
        fileRepo.save(foundFile);
        return foundFile;
    }

    @Transactional(rollbackFor = {IOException.class, FileNotFoundException.class})
    public void deleteFile(String filename) throws IOException {
        File foundFile = fileRepo.findByName(filename);
        if (foundFile.getSize() > 0) {
            fileManager.deleteFile(foundFile.getKey());
        } else {
            throw new FileNotFoundException();
        }
        fileRepo.delete(foundFile);
    }
}
