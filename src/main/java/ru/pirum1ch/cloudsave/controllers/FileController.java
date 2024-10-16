package ru.pirum1ch.cloudsave.controllers;

import org.apache.tomcat.jni.FileInfo;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.pirum1ch.cloudsave.models.File;
import ru.pirum1ch.cloudsave.services.FileService;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/cloud/file")
public class FileController {

    private FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping
    public ResponseEntity<File> upload (@RequestParam MultipartFile file){
        try {
            return new ResponseEntity<>(fileService.upload(file), HttpStatus.ACCEPTED);
        }catch (IOException ioException) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping
    public ResponseEntity<File> delete(String file){
        return null;
    }

    @GetMapping
    public ResponseEntity<Resource> getFileByName(@RequestParam String fileName){
        try {
            Resource resource = fileService.download(fileName);
//            String foundFile = resource.getFile().getName();
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + resource.getFilename())
                    .body(resource);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    public ResponseEntity<File> updateFile (String file){
        return null;
    }
}
