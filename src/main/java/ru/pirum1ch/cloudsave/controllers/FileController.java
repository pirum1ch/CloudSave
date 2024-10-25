package ru.pirum1ch.cloudsave.controllers;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.pirum1ch.cloudsave.models.File;
import ru.pirum1ch.cloudsave.services.FileService;

import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@Log4j2
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * Загрузка файла в хранилище. Если загружается файл,
     * а в хранилище уже есть файл с таким именем - файл будет заменен
     *
     * @param filename
     * @param file
     * @return
     */

    @PostMapping
    public ResponseEntity<File> upload(@RequestParam("filename") String filename, @RequestParam MultipartFile file) throws IllegalArgumentException, IOException {
        log.log(Level.INFO, "Загрузка файла начата");
        if (fileService.getFileByName(filename) == null) {
            log.log(Level.INFO, "Файл новый, загружаем.");
            return new ResponseEntity<>(fileService.upload(filename, file), HttpStatus.OK);
        } else {
            log.log(Level.INFO, "Найден файл с аналогичным именем, обнволяем");
            return new ResponseEntity<>(fileService.fileUpdateByName(filename, file), HttpStatus.OK);
        }
    }

    /**
     * Удаление файла по имени
     *
     * @param fileName
     * @return
     */
    @DeleteMapping
    public ResponseEntity<File> delete(@RequestParam("filename") String fileName) throws IOException{
        log.log(Level.INFO, "Удаление файла: " + fileName);
        fileService.deleteFile(fileName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Скачивание файла по имени
     *
     * @param fileName
     * @return
     */
    @GetMapping
    public ResponseEntity<Resource> downloadFile(@RequestParam("filename") String fileName) throws IllegalArgumentException, IOException {
        log.log(Level.INFO, "Скачиваем файл: " + fileName);
        Resource resource = fileService.download(fileName);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + resource.getFilename())
                .body(resource);
    }

    /**
     * Изменение имени файла
     *
     * @param fileName
     * @param name
     * @return
     */
    @PutMapping
    public ResponseEntity<File> fileNameUpdate(@RequestParam("filename") String fileName, @RequestBody String name) throws IllegalArgumentException, IOException {
        log.log(Level.INFO, "Изменяем имя для файла: " + fileName);
        return new ResponseEntity<>(fileService.fileNameUpdate(fileName, name), HttpStatus.OK);
    }
}
