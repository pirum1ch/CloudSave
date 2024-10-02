package ru.pirum1ch.cloudsave.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pirum1ch.cloudsave.models.File;

import java.util.List;

@RestController
@RequestMapping("/list")
public class ListController {



    @GetMapping
    public List<File> getAllFiles (){
        return null;
    }
}
