package ru.pirum1ch.cloudsave.controllers;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/file")
public class UploadController {

    @PostMapping
    public String upload (String file){
        return null;
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
