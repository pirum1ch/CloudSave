package ru.pirum1ch.cloudsave.controllers;

import io.jsonwebtoken.ExpiredJwtException;
import io.minio.errors.*;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.pirum1ch.cloudsave.dto.FileDto;
import ru.pirum1ch.cloudsave.models.File;
import ru.pirum1ch.cloudsave.services.MinioFileService;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@Log4j2
@RequestMapping("/file")
public class FileController {

    private final MinioFileService fileService;

    public FileController(MinioFileService fileService) {
        this.fileService = fileService;
    }

//    @GetMapping
//    public ResponseEntity<Page<File>> listFiles(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int limitOfFiles,  @RequestParam(defaultValue = "id,asc") String[] sort){
//        Sort sorting = Sort.by(sort[0]);
//        Pageable pageable = PageRequest.of(page, limitOfFiles, sorting);
//        Page<File> filePage = fileService.getListOfAllFiles(pageable);
//        return ResponseEntity.ok(filePage);
//    }

    /**
     * Загрузка файла в хранилище. Если загружается файл,
     * а в хранилище уже есть файл с таким именем - файл будет заменен
     *
     * @param file
     * @return
     */

    @PostMapping
    public ResponseEntity<?> upload(@RequestParam MultipartFile[] file)
            throws IllegalArgumentException, IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException, ExecutionException, InterruptedException {
        fileService.upload(file);
        return ResponseEntity.ok().body("File(s) has been uploaded successfully");
    }

    /**
     * Удаление файла по имени
     *
     * @param fileName
     * @return
     */
    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam("filename") String fileName)
            throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException,
            InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        log.info("Удаление файла: " + fileName);
        fileService.deleteFile(fileName);
        return new ResponseEntity<>("Файл удален", HttpStatus.OK);
    }

    /**
     * Скачивание файла по имени
     *
     * @param fileName
     * @return
     */
    @GetMapping
    public ResponseEntity<?> downloadFile(@RequestParam("filename") String fileName)
            throws IllegalArgumentException, IOException, ServerException, InsufficientDataException,
            ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException {
        //TODO if file exists
        log.debug("Скачиваем файл: " + fileName);
        fileService.download(fileName);
        return new ResponseEntity<>("Файл " + fileName + " скачан", HttpStatus.OK);
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
        log.info("Изменяем имя для файла: " + fileName);
        return new ResponseEntity<>(fileService.fileNameUpdate(fileName, name), HttpStatus.OK);
    }
}
