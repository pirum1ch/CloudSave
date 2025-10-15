package ru.pirum1ch.cloudsave.controllers;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.pirum1ch.cloudsave.models.File;
import ru.pirum1ch.cloudsave.services.MinioFileService;

@RestController
@Log4j2
@RequestMapping("/list")
public class ListController {

    private final MinioFileService fileService;

    public ListController(MinioFileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping
    public ResponseEntity<Page<File>> getAllFiles (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limitOfFiles,
            @RequestParam(defaultValue = "id") String sort)
            throws RuntimeException
    {
            log.log(Level.INFO, "Поиск доступных файлов. Количество не более: " + limitOfFiles);
        Sort sorting = Sort.by(sort).ascending();
        Pageable pageable = PageRequest.of(page, limitOfFiles, sorting);
        Page<File> filePage = fileService.getListOfAllFiles(pageable);
        return ResponseEntity.ok(filePage);
    }
}
