package ru.pirum1ch.cloudsave.controllers;

import org.apache.tomcat.jni.FileInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.pirum1ch.cloudsave.services.FileService;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/file")
public class UploadController {

    private FileService fileService;

    public UploadController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping
    public ResponseEntity<FileInfo> upload (@RequestParam MultipartFile file){
        try {
            return new ResponseEntity<>(fileService.upload(file), HttpStatus.ACCEPTED);
        }catch (IOException ioException) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping
    public String delite(String file){
        return null;
    }

    @GetMapping
    public List<String> getListOfAllFiles(){
        return null;
    }

    @PutMapping
    public String updateFile (String file){
        return null;
    }
}
