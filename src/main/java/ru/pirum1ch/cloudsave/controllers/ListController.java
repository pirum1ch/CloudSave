package ru.pirum1ch.cloudsave.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.pirum1ch.cloudsave.dto.FileDto;
import ru.pirum1ch.cloudsave.models.File;
import ru.pirum1ch.cloudsave.services.FileService;

import java.util.List;

@RestController
@RequestMapping("/list")
public class ListController {

    private final FileService fileService;

    public ListController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping
    public List<FileDto> getAllFiles (@RequestParam("limit") int limit){
        return fileService.getListOfAllFiles(limit);
    }
}
