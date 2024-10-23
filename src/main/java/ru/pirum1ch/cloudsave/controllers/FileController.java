package ru.pirum1ch.cloudsave.controllers;

import org.apache.tomcat.jni.FileInfo;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.pirum1ch.cloudsave.models.File;
import ru.pirum1ch.cloudsave.services.FileService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {

    private FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping
    public ResponseEntity<File> upload (@RequestParam("filename") String filename, @RequestParam MultipartFile file){
        try {
            return new ResponseEntity<>(fileService.upload(file), HttpStatus.ACCEPTED);
        }catch (IOException ioException) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping
    public ResponseEntity<File> delete(@RequestParam ("filename") String fileName){
        try{
            fileService.deleteFile(fileName);
        }catch (FileNotFoundException notFoundException){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (IOException ioException){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping
    public ResponseEntity<Resource> downloadFile(@RequestParam("filename") String fileName){
        try {
            Resource resource = fileService.download(fileName);
            return ResponseEntity
                    .ok()
                    .header("Content-Disposition", "attachment; filename=" + resource.getFilename())
                    .body(resource);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    public ResponseEntity<File> editFileName (@RequestParam ("filename") String fileName, @RequestParam MultipartFile file){
        try{
            fileService.fileUpdateByName(fileName, file);
        }catch (FileNotFoundException notFoundException){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (IOException ioException){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
