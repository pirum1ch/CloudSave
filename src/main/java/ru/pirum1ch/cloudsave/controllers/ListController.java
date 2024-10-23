package ru.pirum1ch.cloudsave.controllers;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.pirum1ch.cloudsave.dto.FileDto;
import ru.pirum1ch.cloudsave.services.FileService;

import java.util.List;

@RestController
@Log4j2
@RequestMapping("/list")
public class ListController {

    private final FileService fileService;

    public ListController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping
    public ResponseEntity<List<FileDto>> getAllFiles (@RequestParam("limit") int limit){
        try {
            log.log(Level.INFO, "Поиск доступных файлов. Количество не более: " + limit);
            return new ResponseEntity<>(fileService.getListOfAllFiles(limit), HttpStatus.OK);
        }catch (NumberFormatException numberFormatException){
            log.log(Level.INFO, numberFormatException.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (RuntimeException runtimeException){
            log.log(Level.INFO, runtimeException.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
